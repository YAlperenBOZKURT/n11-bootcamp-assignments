# n11 Bootcamp Assignments

<details open>
<summary><strong>Assignment - 1: Payment Service - 20.04.2026</strong></summary>

I built a simple payment service with Spring Boot. There's a small HTML/JS UI on top of the REST API where the user picks a payment method and enters an amount and a confirmation message gets printed to the screen on payment.

## Project Structure - JDK 21 / Maven / Spring Boot 3.5.13

```text
src/main/java/com/n11bootcamp/paymentservice/
├── PaymentserviceApplication.java
├── domain/
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
    └── controller/
        └── PaymentController.java

src/main/resources/
├── static/
│   ├── index.html
│   ├── app.js
│   └── style.css
├── application.properties
└── data.sql
```

## Goal

I built the payment system around SOLID principles. Whenever a new payment method needs to be added, **no existing code has to change**: you add a row to the `payment_method_types` table and write a service that implements the `PaymentService` interface — the system picks up the new method automatically. The whole design is built directly on **Open/Closed Principle (OCP)** and **Single Responsibility Principle (SRP)**.

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

```java
public PaymentResponse pay(BigDecimal amount, Long paymentMethodTypeId) {
    PaymentMethodType type = paymentMethodTypeService.getById(paymentMethodTypeId);
    return paymentServiceFactory.getService(type.getCode()).processPayment(amount, type);
}
```

### 6) Logging with AOP (Before / After Payment)

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

### 7) Frontend

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

## Proje Yapısı - JDK 21 / Maven / Spring Boot 3.5.13

```text
src/main/java/com/n11bootcamp/paymentservice/
├── PaymentserviceApplication.java
├── domain/
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
    └── controller/
        └── PaymentController.java

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

```java
public PaymentResponse pay(BigDecimal amount, Long paymentMethodTypeId) {
    PaymentMethodType type = paymentMethodTypeService.getById(paymentMethodTypeId);
    return paymentServiceFactory.getService(type.getCode()).processPayment(amount, type);
}
```

### 6) AOP ile Loglama (Ödeme Öncesi / Sonrası)

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

### 7) Önyüz

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
