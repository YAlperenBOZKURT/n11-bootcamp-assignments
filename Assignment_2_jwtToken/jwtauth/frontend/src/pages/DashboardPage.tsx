import { useEffect, useState } from 'react';
import axiosInstance from '../api/axiosInstance';

interface TokenInfo {
  accessTokenExpiresAt: number;
  refreshTokenExpiresAt: number;
}

const formatDuration = (ms: number): string => {
  if (ms <= 0) return '00:00';
  const totalSeconds = Math.floor(ms / 1000);
  const days = Math.floor(totalSeconds / 86400);
  const hours = Math.floor((totalSeconds % 86400) / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  const seconds = totalSeconds % 60;

  if (days > 0) return `${days}g ${hours}s ${minutes}dk`;
  if (hours > 0) return `${hours}s ${minutes}dk ${seconds}sn`;
  return `${minutes}:${String(seconds).padStart(2, '0')}`;
};

const DashboardPage = () => {
  const [tokenInfo, setTokenInfo] = useState<TokenInfo | null>(null);
  const [now, setNow] = useState(Date.now());

  const fetchTokenInfo = () => {
    axiosInstance
      .get('/auth/token-info')
      .then((res) => setTokenInfo(res.data.data))
      .catch(() => {});
  };

  useEffect(() => {
    fetchTokenInfo();
    const tick = setInterval(() => setNow(Date.now()), 1000);
    return () => clearInterval(tick);
  }, []);

  // access expire olduktan sonra yeniden çek — interceptor otomatik refresh tetikler
  useEffect(() => {
    if (!tokenInfo) return;
    const msUntilExpiry = tokenInfo.accessTokenExpiresAt - Date.now();
    if (msUntilExpiry <= 0) return;

    const timer = setTimeout(fetchTokenInfo, msUntilExpiry + 500);
    return () => clearTimeout(timer);
  }, [tokenInfo?.accessTokenExpiresAt]);

  const handleLogout = async () => {
    try {
      await axiosInstance.post('/auth/logout');
    } finally {
      window.location.href = '/login';
    }
  };

  const accessRemaining = tokenInfo ? tokenInfo.accessTokenExpiresAt - now : 0;
  const refreshRemaining = tokenInfo ? tokenInfo.refreshTokenExpiresAt - now : 0;
  const accessExpired = accessRemaining <= 0;

  return (
    <div className="min-h-screen flex items-center justify-center bg-[#0B1437] px-4 py-10">
      <div className="w-full max-w-lg rounded-2xl bg-white/95 backdrop-blur p-10 shadow-2xl ring-1 ring-black/5 text-center">
        <div className="mx-auto mb-5 flex h-14 w-14 items-center justify-center rounded-2xl bg-emerald-100 text-emerald-600">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" className="h-7 w-7">
            <polyline points="20 6 9 17 4 12" />
          </svg>
        </div>
        <h1 className="text-3xl font-semibold tracking-tight text-slate-900 mb-2">
          Dashboard
        </h1>
        <p className="text-slate-500 mb-8">
          Hoş geldin! Başarıyla giriş yaptın.
        </p>

        {tokenInfo && (
          <div className="mb-8 space-y-3 text-left">
            <div className="rounded-xl border border-slate-200 p-4">
              <div className="flex items-center justify-between">
                <span className="text-sm font-medium text-slate-600">Access Token</span>
                <span className={`text-xs px-2 py-0.5 rounded-full ${accessExpired ? 'bg-amber-100 text-amber-700' : 'bg-emerald-100 text-emerald-700'}`}>
                  {accessExpired ? 'yenileniyor' : 'aktif'}
                </span>
              </div>
              <div className="mt-1 text-2xl font-mono font-semibold text-slate-900">
                {formatDuration(accessRemaining)}
              </div>
              <div className="text-xs text-slate-400 mt-0.5">kalan süre</div>
            </div>

            <div className="rounded-xl border border-slate-200 p-4">
              <div className="flex items-center justify-between">
                <span className="text-sm font-medium text-slate-600">Refresh Token</span>
                <span className="text-xs px-2 py-0.5 rounded-full bg-indigo-100 text-indigo-700">
                  aktif
                </span>
              </div>
              <div className="mt-1 text-2xl font-mono font-semibold text-slate-900">
                {formatDuration(refreshRemaining)}
              </div>
              <div className="text-xs text-slate-400 mt-0.5">kalan süre</div>
            </div>
          </div>
        )}

        <button
          onClick={handleLogout}
          className="inline-flex items-center justify-center rounded-lg bg-slate-900 text-white px-6 py-2.5 font-medium shadow-sm hover:bg-slate-800 active:bg-slate-950 transition"
        >
          Çıkış Yap
        </button>
      </div>
    </div>
  );
};

export default DashboardPage;
