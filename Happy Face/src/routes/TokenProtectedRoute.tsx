import React from 'react';
import { Navigate } from 'react-router-dom';
import {useTokenAuth} from "@/hooks/useTokenAuth.ts";

interface TokenProtectedRouteProps {
  children: React.ReactNode;
}

const TokenProtectedRoute: React.FC<TokenProtectedRouteProps> = ({ children }) => {
  const { token } = useTokenAuth();

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
};

export default TokenProtectedRoute;
