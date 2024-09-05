import React, {createContext, useState, ReactNode} from 'react';
import {TokenAuthContextType} from "@/models/auth/TokenAuthContextType.ts";

export const TokenAuthContext = createContext<TokenAuthContextType | undefined> (undefined);

export const TokenAuthProvider: React.FC<{ children: ReactNode }> = ({children}) => {
    const [token, setToken] = useState<string | null> (localStorage.getItem ('appToken'));

    const setTokenAndStore = (newToken: string | null): void => {
        setToken (newToken);
        if (newToken) {
            localStorage.setItem ('appToken', newToken);
        } else {
            localStorage.removeItem ('appToken');
        }
    };

    return (
        <TokenAuthContext.Provider value={{token, setToken: setTokenAndStore}}>
            {children}
        </TokenAuthContext.Provider>
    );
};