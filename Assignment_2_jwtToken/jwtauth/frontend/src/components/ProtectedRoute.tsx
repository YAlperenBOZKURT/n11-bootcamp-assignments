import { useEffect, useState } from 'react';
import axiosInstance from '../api/axiosInstance';

const ProtectedRoute = ({ children }: { children: React.ReactNode }) => {
  const [loading, setLoading] = useState(true);
  const [authenticated, setAuthenticated] = useState(false);

  useEffect(() => {
    axiosInstance.get('/auth/me')
      .then(() => setAuthenticated(true))
      .catch(() => {
        window.location.href = '/login';
      })
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-gray-500">Yükleniyor...</p>
      </div>
    );
  }

  return authenticated ? <>{children}</> : null;
};

export default ProtectedRoute;