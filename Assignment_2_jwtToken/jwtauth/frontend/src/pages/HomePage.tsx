const HomePage = () => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-[#0B1437] px-4">
      <div className="w-full max-w-lg rounded-2xl bg-white/95 backdrop-blur p-10 shadow-2xl ring-1 ring-black/5 text-center">
        <div className="mx-auto mb-6 flex h-14 w-14 items-center justify-center rounded-2xl bg-indigo-100 text-indigo-600 text-2xl font-bold">
          JWT
        </div>
        <h1 className="text-3xl font-semibold tracking-tight text-slate-900 mb-3">
          JWT Auth Demo
        </h1>
        <p className="text-slate-500 leading-relaxed mb-8">
          Refresh Token mimarisi ile güvenli kimlik doğrulama örneği.
        </p>
        <div className="flex flex-col sm:flex-row gap-3 justify-center">
          <a
            href="/login"
            className="flex-1 inline-flex items-center justify-center rounded-lg bg-indigo-600 px-6 py-2.5 text-white font-medium shadow-sm hover:bg-indigo-700 active:bg-indigo-800 transition"
          >
            Giriş Yap
          </a>
          <a
            href="/register"
            className="flex-1 inline-flex items-center justify-center rounded-lg border border-slate-200 bg-white px-6 py-2.5 text-slate-700 font-medium hover:bg-slate-50 active:bg-slate-100 transition"
          >
            Kayıt Ol
          </a>
        </div>
      </div>
    </div>
  );
};

export default HomePage;
