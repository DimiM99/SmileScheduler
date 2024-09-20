import {useContext} from "react";
import {AuthContext} from "@/contexts/AuthContext.tsx";
import {AuthContextType} from "@/models/components/auth/AuthContextType.ts";

export const useAuth = (): AuthContextType => {
    const context = useContext (AuthContext);
    if (context === undefined) {
        throw new Error ('useAuth must be used within an AuthProvider');
    }
    return context;
};