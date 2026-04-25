# n11 Bootcamp Assignments



<details open>
<summary><strong> Assignment - 2:JWT Auth — Refresh Token Demo - 25.04.2026</strong></summary>

<details open>
<summary><strong>English</strong></summary>

I built a project around how JWT tokens work using Spring Boot and React. A user can register, log in, visit a protected dashboard and log out. Tokens are kept in **HttpOnly cookies**; when the access token expires, the refresh token silently gets a new one in the background. The dashboard also shows a live countdown of both tokens' remaining lifetime.

## Project Structure - JDK 21 / Maven / Spring Boot 3.5.13 / React 19 / Vite / TypeScript

```text
jwtauth/
├── backend/
│   └── src/main/java/com/yabozkurt/jwtauth/
│       ├── JwtauthApplication.java
│       ├── domain/
│       │   ├── exception/
│       │   │   ├── UserAlreadyExistsException.java
│       │   │   ├── UserNotFoundException.java
│       │   │   ├── InvalidRefreshTokenException.java
│       │   │   └── MissingRefreshTokenException.java
│       │   ├── model/
│       │   │   ├── User.java
│       │   │   └── enums/Role.java
│       │   └── repository/
│       │       └── UserRepository.java
│       ├── application/
│       │   ├── dto/
│       │   │   ├── LoginRequest.java
│       │   │   ├── RegisterRequest.java
│       │   │   ├── TokenResponse.java
│       │   │   └── TokenInfoResponse.java
│       │   └── service/
│       │       ├── AuthService.java
│       │       └── impl/AuthServiceImpl.java
│       ├── infrastructure/
│       │   ├── config/SecurityConfig.java
│       │   └── security/
│       │       ├── JwtTokenManager.java
│       │       ├── JwtAuthFilter.java
│       │       ├── CookieHelper.java
│       │       └── CustomUserDetailsService.java
│       └── presentation/
│           ├── controller/AuthController.java
│           ├── dto/
│           │   ├── ApiResponse.java
│           │   └── ApiErrorResponse.java
│           └── exception/GlobalExceptionHandler.java
│
└── frontend/
    └── src/
        ├── main.tsx
        ├── App.tsx
        ├── api/axiosInstance.ts
        ├── components/ProtectedRoute.tsx
        └── pages/
            ├── HomePage.tsx
            ├── LoginPage.tsx
            ├── RegisterPage.tsx
            └── DashboardPage.tsx
```

## Goal

I built the auth flow around a **15 min access token + 7 day refresh token**, both stored in HttpOnly cookies. The browser never touches the token directly, so XSS can't read it from `localStorage`. The frontend never has to think about token lifetime either: when a request returns 401, an axios interceptor silently calls `/auth/refresh` and retries. The whole thing is organized in layers (domain / application / infrastructure / presentation) so that each concern lives in one place and can change on its own.

## How It Works

### 1) Domain model

I started with a single `User` entity. A user has an email, a bcrypt-hashed password and a role.

```java
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role; // USER, ADMIN
}
```

Role is an enum — simpler than a separate table for a two-role project and Spring Security maps it straight to `ROLE_USER` / `ROLE_ADMIN`.

### 2) Two tokens, not one — and they're distinguishable

I split the JWTs into two kinds on purpose:

- **Access token** → short-lived (15 min). Sent on every request.
- **Refresh token** → long-lived (7 days). Only touched by `/auth/refresh`.

Both tokens are signed with the same key but carry a `type` claim (`access` or `refresh`). This matters: without it, a stolen refresh token could be passed as an access token and the filter would happily authenticate it. The filter now rejects anything whose `type` isn't `access` and `/auth/refresh` rejects anything whose `type` isn't `refresh`.

```java
private String buildToken(String email, long expiration, String type) {
    return Jwts.builder()
            .subject(email)
            .claim(TOKEN_TYPE_CLAIM, type)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey())
            .compact();
}
```

If an attacker somehow grabs an access token, the damage window is tiny. The refresh token stays HttpOnly and only leaves the browser when explicitly asked for.

### 3) HttpOnly cookies instead of Authorization headers

I went with cookies because `HttpOnly` cookies can't be read by JavaScript, which closes the XSS token-theft path. `CookieHelper` is the single place that knows cookie names, builds them, reads them back off the request and clears them. Every other class that needs cookies goes through this helper — cookie name strings never leak outside.

```java
public void writeAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) { ... }
public void writeAccessTokenCookie(HttpServletResponse response, String accessToken) { ... }
public String readAccessToken(HttpServletRequest request) { ... }
public String readRefreshToken(HttpServletRequest request) { ... }
public void clearCookies(HttpServletResponse response) { ... }
```

### 4) The JWT filter

On every request, `JwtAuthFilter` pulls `accessToken` from `CookieHelper.readAccessToken`, validates it (signature + expiry + `type == access`), loads the user and puts an `Authentication` into the `SecurityContext`. Any JWT exception (expired, malformed, wrong signature) is swallowed by a try-catch — the filter just falls through and the security chain returns 401 if the endpoint requires auth.

```java
try {
    if (jwtTokenManager.isTokenValid(token) && jwtTokenManager.isAccessToken(token)) {
        // load user, set SecurityContext
    }
} catch (Exception ignored) {
    // invalid/expired token: skip auth, spring returns 401 if endpoint is protected
}
filterChain.doFilter(request, response);
```

### 5) Stateless, session-free security

Spring Security is configured as stateless. No `JSESSIONID`, no server-side session. `/auth/register`, `/auth/login`, `/auth/refresh`, `/auth/logout` are public; everything else (`/auth/me`, `/auth/token-info`, any future endpoint) requires authentication.

```java
http
    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
    .csrf(csrf -> csrf.disable())
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/auth/register", "/auth/login", "/auth/refresh", "/auth/logout").permitAll()
        .anyRequest().authenticated()
    )
    .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
```

CORS is configured with `allowCredentials = true` so the browser actually sends the cookies to the Vite dev server.

### 6) Standardized responses + global error handling

Every response is wrapped in `ApiResponse<T>` and failures are caught centrally by `GlobalExceptionHandler`. No try-catch in the controllers. 409 for "user already exists", 404 for "user not found", **401 for missing or invalid refresh tokens** so the frontend's interceptor can redirect to login cleanly and 500 for anything unexpected.

```java
@ExceptionHandler(InvalidRefreshTokenException.class)
public ResponseEntity<ApiErrorResponse> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ApiErrorResponse(401, ex.getMessage()));
}
```

### 7) Silent refresh on the frontend

The React side never sees the tokens, they're HttpOnly. But when the access token expires while the user is inside the app, surfacing that 401 as an error would hurt the UX. So the axios instance has a response interceptor: on a 401, it tries `/auth/refresh` once and if that works, it retries the original request. If refresh fails too, it bounces the user to `/login`.

```ts
axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        await axiosInstance.post('/auth/refresh');
        return axiosInstance(originalRequest);
      } catch (refreshError) {
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);
```

The `_retry` flag is there to stop infinite loops if `/auth/refresh` itself returns 401.

### 8) Protected routes

`ProtectedRoute` guards pages that require auth. When a logged-out user tries to reach a members-only URL, it calls `/auth/me` and checks the result. If it succeeds, the user goes to the page; otherwise they're redirected to the login page. The `/me` endpoint itself just returns `OK` — its only job is to make the filter run, so the status code is the actual answer.

### 9) Live token lifecycle on the dashboard

To make the invisible refresh mechanism visible, the dashboard shows a live countdown for both tokens. Since the tokens are HttpOnly (JS can't decode them), the backend exposes `GET /auth/token-info` which reads both cookies and returns their `exp` claims. The service extracts the timestamps:

```java
public TokenInfoResponse getTokenInfo(String accessToken, String refreshToken) {
    long accessExp = jwtTokenManager.extractExpiration(accessToken);
    long refreshExp = jwtTokenManager.extractExpiration(refreshToken);
    return new TokenInfoResponse(accessExp, refreshExp);
}
```

The frontend ticks every second and right after the access token hits zero it re-fetches `/auth/token-info`. That re-fetch returns 401 → interceptor refreshes → the retry returns fresh expiry timestamps. You can watch the access timer reset itself on the screen.

## Auth Flow At a Glance

1. **Register** → `POST /auth/register` with email + password → user created, password bcrypt-hashed.
2. **Login** → `POST /auth/login` → server signs two JWTs (with `type` claims), sets them as HttpOnly cookies.
3. **Protected request** → cookie goes with the request automatically, `JwtAuthFilter` validates it (signature + expiry + `type == access`).
4. **Access expired** → server returns 401 → axios interceptor calls `/auth/refresh` → server validates refresh token (`type == refresh`) → new access cookie is set → original request retried.
5. **Dashboard countdown** → `GET /auth/token-info` returns expiry timestamps; a `setInterval` draws the remaining time.
6. **Logout** → `POST /auth/logout` → both cookies cleared with `maxAge = 0`.

## Requirements

- JDK 21
- Maven
- Node 20+
- Docker (for PostgreSQL)

## Running

1. **Database:** From `backend/`, run `docker compose up -d` to start PostgreSQL on port 5432. If there's a port conflict, edit `compose.yaml` and update the matching URL in `application.yaml`.
2. **Backend:** In `backend/`, run `./mvnw spring-boot:run` (or start it from your IDE). Server comes up on `http://localhost:8080`. A seed admin (`admin@gmail.com`, password `admin123`) is inserted on first boot via `data.sql`.
3. **Frontend:** In `frontend/`, run `npm install` once, then `npm run dev`. The Vite dev server is on `http://localhost:5173`.
4. **Test:** Open `http://localhost:5173`, register a new user or log in with the seeded admin and you should land on `/dashboard`.

</details>

<details>
<summary><strong>Türkçe</strong></summary>

Spring Boot ve React ile JWT token çalışma mantığı ile alakalı bir proje yaptım. Kullanıcı kayıt olabiliyor, giriş yapabiliyor, korumalı bir dashboard sayfasına gidebiliyor ve çıkış yapabiliyor. Token'lar **HttpOnly cookie** olarak tutuluyor; access token süresi dolduğunda refresh token arka planda sessizce yenisini alıyor. Dashboard ayrıca iki token'ın kalan sürelerini canlı olarak gösteriyor.

## Proje Yapısı - JDK 21 / Maven / Spring Boot 3.5.13 / React 19 / Vite / TypeScript

```text
jwtauth/
├── backend/
│   └── src/main/java/com/yabozkurt/jwtauth/
│       ├── JwtauthApplication.java
│       ├── domain/
│       │   ├── exception/
│       │   │   ├── UserAlreadyExistsException.java
│       │   │   ├── UserNotFoundException.java
│       │   │   ├── InvalidRefreshTokenException.java
│       │   │   └── MissingRefreshTokenException.java
│       │   ├── model/
│       │   │   ├── User.java
│       │   │   └── enums/Role.java
│       │   └── repository/
│       │       └── UserRepository.java
│       ├── application/
│       │   ├── dto/
│       │   │   ├── LoginRequest.java
│       │   │   ├── RegisterRequest.java
│       │   │   ├── TokenResponse.java
│       │   │   └── TokenInfoResponse.java
│       │   └── service/
│       │       ├── AuthService.java
│       │       └── impl/AuthServiceImpl.java
│       ├── infrastructure/
│       │   ├── config/SecurityConfig.java
│       │   └── security/
│       │       ├── JwtTokenManager.java
│       │       ├── JwtAuthFilter.java
│       │       ├── CookieHelper.java
│       │       └── CustomUserDetailsService.java
│       └── presentation/
│           ├── controller/AuthController.java
│           ├── dto/
│           │   ├── ApiResponse.java
│           │   └── ApiErrorResponse.java
│           └── exception/GlobalExceptionHandler.java
│
└── frontend/
    └── src/
        ├── main.tsx
        ├── App.tsx
        ├── api/axiosInstance.ts
        ├── components/ProtectedRoute.tsx
        └── pages/
            ├── HomePage.tsx
            ├── LoginPage.tsx
            ├── RegisterPage.tsx
            └── DashboardPage.tsx
```

## Amaç

Kimlik doğrulama akışını **15 dk access token + 7 günlük refresh token** üzerine kurdum; ikisi de HttpOnly cookie olarak saklanıyor. Tarayıcı token'a doğrudan erişemediği için XSS ile `localStorage`'tan çalınma riski ortadan kalkıyor. Frontend de token ömrüyle uğraşmak zorunda değil: bir istek 401 dönerse axios interceptor'ı sessizce `/auth/refresh` çağırıp isteği tekrarlıyor. Proje katmanlara ayrılmış (domain / application / infrastructure / presentation) — her sorumluluk tek bir yerde yaşıyor ve ayrı ayrı değişebiliyor.

## Çalışma Mantığı

### 1) Domain modeli

Tek bir `User` entity ile başladım. Kullanıcının bir email'i, bcrypt ile hash'lenmiş şifresi ve bir rolü var.

```java
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role; // USER, ADMIN
}
```

Rol bir enum — iki rollü bir proje için ayrı bir tablo yapmak aşırı kaçardı, Spring Security de bunu doğrudan `ROLE_USER` / `ROLE_ADMIN`'e map'liyor.

### 2) Tek token değil, iki token ve birbirinden ayırt edilebilir

JWT'leri bilinçli olarak ikiye ayırdım:

- **Access token** → kısa ömürlü (15 dk). Her istekte gönderiliyor.
- **Refresh token** → uzun ömürlü (7 gün). Sadece `/auth/refresh` tarafından okunuyor.

İkisi de aynı anahtarla imzalı ama içlerinde `type` claim'i var (`access` veya `refresh`). Bu önemli: olmasa, çalınan bir refresh token access token yerine kullanılabilir ve filter authenticate ederdi. Artık filter `type`'ı `access` olmayan her token'ı reddediyor, `/auth/refresh` de `type`'ı `refresh` olmayanı reddediyor.

```java
private String buildToken(String email, long expiration, String type) {
    return Jwts.builder()
            .subject(email)
            .claim(TOKEN_TYPE_CLAIM, type)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey())
            .compact();
}
```

Saldırgan access token'ı ele geçirse bile etki süresi çok kısa. Refresh token HttpOnly kalıyor ve yalnızca istenerek tarayıcıdan çıkıyor.

### 3) `Authorization` header yerine HttpOnly cookie

Cookie yaklaşımını tercih ettim çünkü `HttpOnly` cookie'yi JavaScript okuyamaz; bu XSS ile token çalınma yolunu kapatıyor. `CookieHelper` cookie isimlerini bilen, cookie üreten, request'ten okuyan ve temizleyen tek sınıf. Cookie'ye ihtiyaç duyan her yer buradan geçiyor. Cookie ismi string'leri başka hiçbir dosyada yok.

```java
public void writeAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) { ... }
public void writeAccessTokenCookie(HttpServletResponse response, String accessToken) { ... }
public String readAccessToken(HttpServletRequest request) { ... }
public String readRefreshToken(HttpServletRequest request) { ... }
public void clearCookies(HttpServletResponse response) { ... }
```


### 4) JWT filter

Her istekte `JwtAuthFilter`, `CookieHelper.readAccessToken` ile access token'ı alıyor, doğruluyor (imza + süre + `type == access`), kullanıcıyı yüklüyor ve `Authentication` nesnesini `SecurityContext`'e koyuyor. Herhangi bir JWT exception'ı (expired, malformed, imza yanlış) try-catch ile yutuluyor, filter auth'suz devam ediyor, endpoint korumalıysa Spring 401 dönüyor. 

```java
try {
    if (jwtTokenManager.isTokenValid(token) && jwtTokenManager.isAccessToken(token)) {
        // kullanıcıyı yükle, SecurityContext'e yaz
    }
} catch (Exception ignored) {
    // token geçersiz/expired: auth atlanır, endpoint korumalıysa spring 401 döner
}
filterChain.doFilter(request, response);
```

### 5) Stateless, session'sız güvenlik

Spring Security stateless çalışacak şekilde yapılandırıldı. Ne `JSESSIONID` ne de server-side session var. `/auth/register`, `/auth/login`, `/auth/refresh`, `/auth/logout` public; geri kalan her şey (`/auth/me`, `/auth/token-info`, sonradan eklenecek her endpoint) auth istiyor.

```java
http
    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
    .csrf(csrf -> csrf.disable())
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/auth/register", "/auth/login", "/auth/refresh", "/auth/logout").permitAll()
        .anyRequest().authenticated()
    )
    .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
```

CORS `allowCredentials = true` ile yapılandırılmış; tarayıcı Vite dev server'ına cookie göndersin diye.

### 6) Standart response + merkezi hata yönetimi

Tüm response'lar `ApiResponse<T>` ile dönülüyor, hatalar `GlobalExceptionHandler` tarafından merkezi olarak yakalanıyor. Controller'larda try-catch yok. "Kullanıcı zaten var" için 409, "bulunamadı" için 404, **refresh token eksik/geçersiz** için 401 (frontend interceptor'ın temiz şekilde login'e yönlendirebilmesi için), beklenmeyen hatalar için 500.

```java
@ExceptionHandler(InvalidRefreshTokenException.class)
public ResponseEntity<ApiErrorResponse> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ApiErrorResponse(401, ex.getMessage()));
}
```


### 7) Frontend'de silent refresh

React tarafı token'ları hiç görmüyor, HttpOnly'ler. Ama access token kullanıcı uygulamanın içindeyken dolarsa, ortaya çıkan 401 hatasını kullanıcıya göstermek kullanıcı deneyimini kötü etkilerdi. Bu yüzden axios instance'ına bir response interceptor koydum: 401 gelirse bir kez `/auth/refresh` deniyor, başarılıysa orijinal isteği tekrarlıyor. Refresh da başarısızsa kullanıcıyı `/login`'e atıyor.

```ts
axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        await axiosInstance.post('/auth/refresh');
        return axiosInstance(originalRequest);
      } catch (refreshError) {
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);
```

`_retry` bayrağı, `/auth/refresh`'in kendisi 401 dönerse sonsuz döngüye girmemek için.

### 8) Korumalı route'lar

`ProtectedRoute` auth isteyen sayfaları koruyor. Kullanıcı giriş yapmadan üyeliğe özel URL'lere ulaşmaya çalıştığında `/auth/me` çağırıyor ve sonucu kontrol ediyor. Başarılıysa kullanıcı o sayfaya gidiyor, değilse login sayfasına yönlendiriyor. `/me` endpoint'i sadece `OK` dönüyor. Tek görevi filter'ı çalıştırmak, asıl cevap status code.

### 9) Dashboard'da canlı token ömrü

Görünmez refresh mekanizmasını görünür kılmak için dashboard iki token'ın kalan süresini canlı gösteriyor. Token'lar HttpOnly olduğu için (JS decode edemez) backend `GET /auth/token-info` endpoint'ini sunuyor; bu endpoint iki cookie'yi okuyup `exp` claim'lerini dönüyor. Service timestamp'leri çıkarıyor:

```java
public TokenInfoResponse getTokenInfo(String accessToken, String refreshToken) {
    long accessExp = jwtTokenManager.extractExpiration(accessToken);
    long refreshExp = jwtTokenManager.extractExpiration(refreshToken);
    return new TokenInfoResponse(accessExp, refreshExp);
}
```

Frontend her saniye tick atıyor; access sayacı sıfıra düştüğü anda `/auth/token-info`'yu yeniden çekiyor. Bu yeni istek 401 döner → interceptor refresh eder → retry yeni expiry timestamp'leri alır. Sayacın kendiliğinden sıfırlandığını ekranda görebiliyorsun.

## Akışın Özeti

1. **Kayıt** → `POST /auth/register` email + şifre ile → kullanıcı oluşturulur, şifre bcrypt ile hash'lenir.
2. **Giriş** → `POST /auth/login` → server iki JWT imzalar (`type` claim'leriyle), HttpOnly cookie olarak set eder.
3. **Korumalı istek** → cookie otomatik gider, `JwtAuthFilter` doğrular (imza + süre + `type == access`).
4. **Access süresi dolduysa** → server 401 döner → axios interceptor `/auth/refresh` çağırır → server refresh token'ı doğrular (`type == refresh`) → yeni access cookie set edilir → orijinal istek tekrarlanır.
5. **Dashboard geri sayım** → `GET /auth/token-info` süreleri döner; `setInterval` kalan zamanı çizer.
6. **Çıkış** → `POST /auth/logout` → iki cookie de `maxAge = 0` ile temizlenir.

## Gereksinimler

- JDK 21
- Maven
- Node 20+
- Docker (PostgreSQL için)

## Çalıştırma

1. **Veritabanı:** `backend/` içinde `docker compose up -d` ile PostgreSQL'i 5432 portunda ayağa kaldırın. Port çakışması varsa `compose.yaml`'ı ve `application.yaml` içindeki URL'yi güncelleyin.
2. **Backend:** `backend/` içinde `./mvnw spring-boot:run` ile (veya IDE'den) çalıştırın. Server `http://localhost:8080`'de açılır. İlk açılışta `data.sql` üzerinden seed admin (`admin@gmail.com`, şifre `admin123`) eklenir.
3. **Frontend:** `frontend/` içinde bir kere `npm install`, ardından `npm run dev`. Vite dev server `http://localhost:5173`'te.
4. **Test:** `http://localhost:5173`'ü açın, yeni bir kullanıcı kaydedin ya da seed admin ile giriş yapın; `/dashboard`'a düşeceksiniz.

</details>

</details>




<details >
<summary><strong>Assignment - 1: Payment Service - 20.04.2026</strong></summary>

<details>
<summary><strong>English</strong></summary>

I built a simple payment service with Spring Boot. There's a small HTML/JS UI on top of the REST API where the user picks a payment method and enters an amount and a confirmation message gets printed to the screen on payment.

This project goes beyond the assignment requirements on purpose. I used it as a chance to practice patterns I'll need in real projects (global error handling, standardized API responses, etc.).

## Project Structure - JDK 21 / Maven / Spring Boot 3.5.13

```text
src/main/java/com/n11bootcamp/paymentservice/
├── PaymentserviceApplication.java
├── domain/
│   ├── exception/
│   │   └── PaymentMethodNotFoundException.java
│   └── model/
│       ├── Payment.java
│       └── PaymentMethodType.java
├── application/
│   ├── dto/
│   │   ├── PaymentResponse.java
│   │   └── PaymentMethodTypeResponse.java
│   ├── factory/
│   │   └── PaymentServiceFactory.java
│   └── service/
│       ├── PaymentService.java
│       ├── PaymentMethodTypeService.java
│       └── impl/
│           ├── CreditCardPaymentServiceImpl.java
│           ├── PaypalPaymentServiceImpl.java
│           └── PaymentMethodTypeServiceImpl.java
├── infrastructure/
│   └── repository/
│       ├── PaymentRepository.java
│       └── PaymentMethodTypeRepository.java
└── presentation/
    ├── controller/
    │   └── PaymentController.java
    ├── dto/
    │   └── ApiResponse.java
    └── exception/
        └── GlobalExceptionHandler.java

src/main/resources/
├── static/
│   ├── index.html
│   ├── app.js
│   └── style.css
├── application.properties
└── data.sql
```

## Goal

I built the payment system around SOLID principles. Whenever a new payment method needs to be added, **no existing code has to change**: you add a row to the `payment_method_types` table and write a service that implements the `PaymentService` interface. The system picks up the new method automatically. The whole design is built directly on **Open/Closed Principle (OCP)** and **Single Responsibility Principle (SRP)**.

## How It Works

I added 2 payment methods as examples: **Credit Card** and **PayPal**.

### 1) Domain models

First I defined two domain entities to represent payment methods and payments. Payment methods live in the database as `PaymentMethodType` rows, so adding a new method is about **adding data**, not code.

```java
public class PaymentMethodType {
    private Long id;
    private String code;          // The key the service identifies itself with (e.g. "CREDIT_CARD")
    private String displayName;   // The name shown in the UI
}

public class Payment {
    private Long id;
    private BigDecimal amount;
    private PaymentMethodType paymentMethodType;
    private LocalDateTime createdAt;
}
```

The `PaymentMethodType.code` field is the critical bit: it's the key each payment service uses to identify itself. That's how the code and the database stay linked.

### 2) Service interfaces

I defined a contract every payment method has to follow (`PaymentService`) and a separate service that manages the list of payment methods (`PaymentMethodTypeService`). The reason for two separate interfaces is **SRP**: one is responsible for "taking payments", the other for "providing payment methods". Merging them into a single interface would mix responsibilities.

```java
public interface PaymentService {
    PaymentResponse processPayment(BigDecimal amount, PaymentMethodType paymentMethodType);
    String getCode();
}
```

### 3) A separate implementation per payment method

I put each payment method into its own class because in real life Credit Card, PayPal, Apple Pay etc. all need totally different APIs, validation flows and error handling. Cramming them into a single class with `if/else` would violate both **SRP** and **OCP** (new method = modifying an existing class).

```java
public class CreditCardPaymentServiceImpl implements PaymentService {

    public String getCode() {
        return "CREDIT_CARD";
    }

    public PaymentResponse processPayment(BigDecimal amount, PaymentMethodType type) {
        // save the payment, return a credit-card-specific message
    }
}
```

`PaypalPaymentServiceImpl` implements the same interface with a different `code` ("PAYPAL"). When I want to add a new method I just drop a new `XyzPaymentServiceImpl` into this folder. I don't touch any other file.

### 4) Routing via Factory

I didn't want to use `if/else` or `switch` to pick which service the controller calls, because every new payment method would force me to change the controller. That'd be an **OCP** violation. So instead I built a **Factory + Registry (Map)**. Spring injects all `PaymentService` beans as a single `List`; I turn that into a `code → service` map.

```java
public class PaymentServiceFactory {

    private final Map<String, PaymentService> services;

    public PaymentServiceFactory(List<PaymentService> serviceList) {
        this.services = serviceList.stream()
                .collect(Collectors.toMap(PaymentService::getCode, s -> s));
    }

    public PaymentService getService(String code) {
        // find the service by code, throw if missing
    }
}
```

This way the Factory doesn't change when a new service is added either. Spring automatically includes the new bean in the Map. This is the actual thing that keeps the design "open for extension, closed for modification".

### 5) Controller

I solved the controller side with a single method: it takes the incoming `paymentMethodTypeId`, fetches the `code` from the DB, asks the Factory and delegates the work to whatever service comes back. So when more payment methods get added later, the flow doesn't have to change. This naturally lines up with the **Dependency Inversion** principle.

Every response is wrapped in `ApiResponse<T>` so that the API always returns a standardized JSON structure. The frontend can always check `success` first, then read `data` or `errorMessage`.

```java
public ApiResponse<PaymentResponse> pay(BigDecimal amount, Long paymentMethodTypeId) {
    PaymentMethodType type = paymentMethodTypeService.getById(paymentMethodTypeId);
    PaymentResponse response = paymentServiceFactory.getService(type.getCode()).processPayment(amount, type);
    return ApiResponse.success(response);
}
```

### 6) Error Handling (Custom Exception + Global Handler)

Instead of throwing generic `RuntimeException`s, the service and factory throw a `PaymentMethodNotFoundException`. A `@RestControllerAdvice` class (`GlobalExceptionHandler`) catches these centrally. No try-catch blocks needed in the controller. The handler returns the appropriate HTTP status code (404 for not found, 500 for unexpected errors) wrapped in the same `ApiResponse` format, so both success and error responses share a consistent structure. It establishes a standard.

### 7) Logging with AOP (Before / After Payment)

Instead of writing the logging into every service, I collected it in a single place with **Aspect-Oriented Programming**.

```java
public class PaymentLoggingAspect {

    @Before("execution(* ...PaymentService+.processPayment(..)) && args(amount, type)")
    public void beforePayment(BigDecimal amount, PaymentMethodType type) {
        System.out.println(type.getDisplayName() + " payment process started (Amount: " + amount + " TL)");
    }

    @AfterReturning(pointcut = "execution(* ...processPayment(..))", returning = "response")
    public void afterPayment(PaymentResponse response) {
        System.out.println("Payment successful → Amount: " + response.getAmount()
                + " TL, Time: " + response.getCreatedAt()
                + ", Method: " + response.getDisplayName());
    }
}
```

`@Before` runs right before the payment method is called, `@AfterReturning` runs after a successful return. Since the pointcut targets the `PaymentService+` interface, every implementation (card, PayPal, anything added later) gets this logging **without any code change**.

### 8) Frontend

I put a simple Vanilla JS / HTML / CSS setup on the Spring Boot side; this both made it easy to test the product visually and saved me from dealing with CORS headaches. The static files live under `src/main/resources/static/`, Spring Boot serves them automatically.

## How To Add a New Payment Method

The real test of this design: how many files do I touch when adding a new method?

1. Add the new payment method to the payment method types table.
2. Add a new service class that implements the `PaymentService` interface.

Controller, Factory, the other services, none of them change.

## Requirements

- JDK 21
- Maven
- Docker (for PostgreSQL)

## Running

1. **Database:** From the root directory, bring up PostgreSQL with `docker compose up -d`. If you have a port conflict, change the port in `compose.yml`.
2. **Application:** Run from your IDE inside the `paymentservice` directory, or `./mvnw spring-boot:run` from the terminal. If port 8080 is taken, change it in `application.properties`.
3. **UI / Test:** Open `http://localhost:8080` in the browser (Spring Boot serves the static HTML automatically), or use Postman with `POST /api/payments/pay`.

</details>

<details>
<summary><strong>Türkçe</strong></summary>

Bu projede Spring Boot ile basit bir ödeme servisi oluşturuldu. REST API üzerine küçük bir HTML/JS arayüz eklenerek kullanıcıdan ödeme yöntemi ve tutar bilgisi alınıyor, ödediğinde ise bilgi mesajı ekrana basılıyor.

Bu proje bilerek ödev gereksinimlerinin ötesine geçiyor. Gerçek projelerde ihtiyaç duyacağım pattern'leri (merkezi hata yönetimi, standart API response yapısı vb.) pratik etmek için fırsat olarak kullandım.

## Proje Yapısı - JDK 21 / Maven / Spring Boot 3.5.13

```text
src/main/java/com/n11bootcamp/paymentservice/
├── PaymentserviceApplication.java
├── domain/
│   ├── exception/
│   │   └── PaymentMethodNotFoundException.java
│   └── model/
│       ├── Payment.java
│       └── PaymentMethodType.java
├── application/
│   ├── dto/
│   │   ├── PaymentResponse.java
│   │   └── PaymentMethodTypeResponse.java
│   ├── factory/
│   │   └── PaymentServiceFactory.java
│   └── service/
│       ├── PaymentService.java
│       ├── PaymentMethodTypeService.java
│       └── impl/
│           ├── CreditCardPaymentServiceImpl.java
│           ├── PaypalPaymentServiceImpl.java
│           └── PaymentMethodTypeServiceImpl.java
├── infrastructure/
│   └── repository/
│       ├── PaymentRepository.java
│       └── PaymentMethodTypeRepository.java
└── presentation/
    ├── controller/
    │   └── PaymentController.java
    ├── dto/
    │   └── ApiResponse.java
    └── exception/
        └── GlobalExceptionHandler.java

src/main/resources/
├── static/
│   ├── index.html
│   ├── app.js
│   └── style.css
├── application.properties
└── data.sql
```

## Amaç

SOLID prensiplerine uygun bir ödeme sistemi kurdum. İleride yeni bir ödeme yöntemi eklenmek istendiğinde **mevcut hiçbir kodu değiştirmek gerekmiyor**: `payment_method_types` tablosuna yeni bir satır eklenip `PaymentService` interface'ini implement eden bir servis yazıldığında sistem otomatik olarak yeni yöntemi tanıyor. Bu doğrudan **Open/Closed Principle (OCP)** ile **Single Responsibility Principle (SRP)** üzerine kurulu bir tasarım.

## Çalışma Mantığı

Örnek olarak 2 adet ödeme yöntemi ekledim: **Kredi Kartı** ve **PayPal**.

### 1) Domain modelleri

Önce ödeme yöntemlerini ve ödemeleri temsil eden iki domain entity tanımladım. Ödeme yöntemleri `PaymentMethodType` veritabanında tutuluyor; bu sayede yeni yöntem tanımlamak için kod değil, **veri eklemek** yeterli oluyor.

```java
public class PaymentMethodType {
    private Long id;
    private String code;          // Servisin kendini tanıttığı anahtar (ör: "CREDIT_CARD")
    private String displayName;   // UI'da gösterilen isim
}

public class Payment {
    private Long id;
    private BigDecimal amount;
    private PaymentMethodType paymentMethodType;
    private LocalDateTime createdAt;
}
```

`PaymentMethodType.code` alanı kritik: ödeme servislerinin kendilerini tanıtacağı anahtar bu. Kod ile veritabanı arasındaki bağ böyle kuruluyor.

### 2) Servis interface'leri

Her ödeme yönteminin uyacağı bir sözleşme (`PaymentService`) ve ödeme yöntemi listesini yöneten ayrı bir servis (`PaymentMethodTypeService`) tanımladım. İki ayrı interface olmasının sebebi **SRP**: birinin sorumluluğu "ödeme almak", diğerinin "ödeme yöntemlerini sağlamak". Tek interface'te birleştirmek sorumlulukları karıştırırdı.

```java
public interface PaymentService {
    PaymentResponse processPayment(BigDecimal amount, PaymentMethodType paymentMethodType);
    String getCode();
}
```

### 3) Her ödeme yöntemi için ayrı implementasyon

Her ödeme yöntemini ayrı bir sınıfta topladım çünkü gerçek hayatta Kredi Kartı, PayPal, Apple Pay gibi sağlayıcılar tamamen farklı API'ler, farklı doğrulama akışları ve farklı hata yönetimleri gerektirir. Bunları tek bir sınıfta `if/else` ile yönetmek hem **SRP**'yi hem de **OCP**'yi ihlal ederdi (yeni yöntem = mevcut sınıfı değiştirmek).

```java
public class CreditCardPaymentServiceImpl implements PaymentService {

    public String getCode() {
        return "CREDIT_CARD";
    }

    public PaymentResponse processPayment(BigDecimal amount, PaymentMethodType type) {
        // ödemeyi kaydet, kredi kartına özel mesajı dön
    }
}
```

`PaypalPaymentServiceImpl` aynı interface'i farklı bir `code` ("PAYPAL") ile implement ediyor. Yeni bir yöntem eklemek istediğimde sadece bu klasöre yeni bir `XyzPaymentServiceImpl` koymam yetiyor — diğer hiçbir dosyaya dokunmuyorum.

### 4) Factory ile yönlendirme

Controller'ın hangi servisi çağıracağını seçerken `if/else` ya da `switch` kullanmak istemedim çünkü her yeni ödeme yönteminde controller'ı değiştirmek zorunda kalırdım. **OCP** ihlali olurdu. Bunun yerine bir **Factory + Registry (Map)** kurdum. Spring tüm `PaymentService` bean'lerini tek bir `List` olarak inject ediyor; ben de bunları `code → service` şeklinde Map'e döküyorum.

```java
public class PaymentServiceFactory {

    private final Map<String, PaymentService> services;

    public PaymentServiceFactory(List<PaymentService> serviceList) {
        this.services = serviceList.stream()
                .collect(Collectors.toMap(PaymentService::getCode, s -> s));
    }

    public PaymentService getService(String code) {
        // koddan servisi bul, yoksa hata fırlat
    }
}
```

Bu sayede yeni servis eklendiğinde Factory de değişmiyor. Spring otomatik olarak yeni bean'i Map'e dahil ediyor. Tasarımın "genişlemeye açık, değişikliğe kapalı" kalmasını sağlayan asıl nokta bu.

### 5) Controller

Controller'da tek metot ile çözdük: gelen `paymentMethodTypeId`'den DB'deki `code`'u alıp Factory'ye soruyor, dönen servise işi devrediyor. Bu sayede ileride başka ödeme yöntemleri eklediğimizde akışta değişiklik yapmak zorunda kalmayacağız. **Dependency Inversion** prensibine uygun olmuş oluyor.

Tüm response'lar `ApiResponse<T>` ile sarmalanıyor; böylece API standart bir JSON yapısı dönüyor. Frontend her zaman önce `success` alanını kontrol edip ardından `data` veya `errorMessage` okuyor.

```java
public ApiResponse<PaymentResponse> pay(BigDecimal amount, Long paymentMethodTypeId) {
    PaymentMethodType type = paymentMethodTypeService.getById(paymentMethodTypeId);
    PaymentResponse response = paymentServiceFactory.getService(type.getCode()).processPayment(amount, type);
    return ApiResponse.success(response);
}
```

### 6) Hata Yönetimi (Custom Exception + Global Handler)

Service ve Factory'de genel `RuntimeException` fırlatmak yerine `PaymentMethodNotFoundException` fırlatılıyor. `@RestControllerAdvice` anotasyonlu `GlobalExceptionHandler` sınıfı bu hataları merkezi olarak yakalıyor. Controller'da try-catch yazmaya gerek kalmıyor. Handler uygun HTTP status code'u (bulunamadı için 404, beklenmeyen hatalar için 500) aynı `ApiResponse` formatında dönüyor; böylece başarılı ve hatalı response'lar tutarlı bir yapıyı paylaşıyor. Standart oluşturuyor.

### 7) AOP ile Loglama (Ödeme Öncesi / Sonrası)

Loglamayı her servisin içine yazmak yerine **Aspect-Oriented Programming** ile tek bir noktada topladım.

```java
public class PaymentLoggingAspect {

    @Before("execution(* ...PaymentService+.processPayment(..)) && args(amount, type)")
    public void beforePayment(BigDecimal amount, PaymentMethodType type) {
        System.out.println(type.getDisplayName() + " yöntemiyle ödeme işlemi başlatıldı (Tutar: " + amount + " TL)");
    }

    @AfterReturning(pointcut = "execution(* ...processPayment(..))", returning = "response")
    public void afterPayment(PaymentResponse response) {
        System.out.println("Ödeme başarılı → Tutar: " + response.getAmount()
                + " TL, Zaman: " + response.getCreatedAt()
                + ", Yöntem: " + response.getDisplayName());
    }
}
```

`@Before` ödeme metodu çağrılmadan hemen önce, `@AfterReturning` ise başarılı dönüşten sonra çalışıyor. Pointcut `PaymentService+` interface'ini hedef aldığı için tüm implementasyonlar (kart, PayPal, ileride eklenecekler) bu loglamayı **kod değişikliği olmadan** alıyor.

### 8) Önyüz

Spring Boot tarafına basit bir Vanilla JS / HTML / CSS yapı kurdum; hem ürünü görsel olarak test etmeyi kolaylaştırdı hem de CORS dertleriyle uğraşmaktan kurtardı. Statik dosyalar `src/main/resources/static/` altında, Spring Boot bunları otomatik servis ediyor.

## Yeni Bir Ödeme Yöntemi Nasıl Eklenir?

Bu tasarımın asıl testi: yeni yöntem eklerken kaç dosyaya dokunuyoruz? 

1. Ödeme yöntem tipleri tablosuna ödeme yöntemini eklememiz lazım.
2. `PaymentService` interface'ini implement eden yeni bir servis sınıfı eklememiz lazım.

Bu sayede Controller, Factory, diğer servisler hiçbiri değişmiyor.

## Gereksinimler

- JDK 21
- Maven
- Docker (PostgreSQL için)

## Çalıştırma

1. **Veritabanı:** Kök dizinde `docker compose up -d` ile PostgreSQL'i ayağa kaldırın. Port çakışması varsa `compose.yml` üzerinden portu değiştirin.
2. **Uygulama:** `paymentservice` dizininde IDE'den çalıştırın ya da terminalde `./mvnw spring-boot:run`. 8080 portu doluysa `application.properties` üzerinden değiştirin.
3. **UI / Test:** Tarayıcıdan `http://localhost:8080` (Spring Boot static HTML'i otomatik servis ediyor) veya Postman ile `POST /api/payments/pay`.

</details>

</details>


