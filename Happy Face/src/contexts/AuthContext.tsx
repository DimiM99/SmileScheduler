import React, {createContext, useState, ReactNode} from 'react';
import {AuthContextType} from "@/models/components/auth/AuthContextType.ts";
import {AuthService} from "@/services/authService.ts";
import {LoginRequest} from "@/models/services/requests/LoginRequest.ts";
import {GetUserResponse} from "@/models/services/responses/GetUserResponse.ts";

export const AuthContext = createContext<AuthContextType | undefined> (undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [user, setUser] = useState<GetUserResponse | null>(null);

    const authService = new AuthService();  // Create instance of AuthService

    const login = async (loginRequest: LoginRequest): Promise<void> => {
        try {
            await authService.login(loginRequest);
            const loggedInUser = await authService.getUser();
            setUser(loggedInUser);

        } catch (error) {
            console.error('Login failed', error);
        }
    };


    const logout = (): void => {
        authService.clearToken();
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};
