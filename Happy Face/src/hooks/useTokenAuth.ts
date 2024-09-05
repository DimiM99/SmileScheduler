import {useContext} from "react";
import {TokenAuthContext} from "@/contexts/TokenAuthContext.tsx";
import {TokenAuthContextType} from "@/models/auth/TokenAuthContextType.ts";

export const useTokenAuth = (): TokenAuthContextType => {
  const context = useContext(TokenAuthContext);
  if (context === undefined) {
    throw new Error('useTokenAuth must be used within a TokenAuthProvider');
  }
  return context;
};
