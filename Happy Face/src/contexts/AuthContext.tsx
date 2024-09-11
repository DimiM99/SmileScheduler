import React, {createContext, useState, ReactNode} from 'react';
import {User} from '@/models';
import {AuthContextType} from "@/models/auth/AuthContextType.ts";

export const AuthContext = createContext<AuthContextType | undefined> (undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({children}) => {
    const [user, setUser] = useState<User | null> (null);

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const login = async (username: string, password: string): Promise<void> => {
    };

    const logout = (): void => {
        setUser (null);
    };

    return (
        <AuthContext.Provider value={{user, login, logout}}>
            {children}
        </AuthContext.Provider>
    );
};