import {User} from "@/models";

export interface AuthContextType {
    user: User | null;
    login: (username: string, password: string) => Promise<void>;
    register: (user: User) => Promise<void>;
    logout: () => void;
}