export interface TokenAuthContextType {
    token: string | null;
    setToken: (token: string | null) => void;
}