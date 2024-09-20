import {GetUserResponse} from "@/models/services/responses/GetUserResponse.ts";
import {LoginResponse} from "@/models/services/responses/LoginResponse.ts";
import {LoginRequest} from "@/models/services/requests/LoginRequest.ts";

export interface IAuthService {
    login(req: LoginRequest): Promise<LoginResponse>;
    getUser(): Promise<GetUserResponse>;
    saveToken(loginResponse: LoginResponse): void;
    clearToken(): void;
    refreshToken(): Promise<void>;
    getDecryptedToken(): Promise<string | null>;
    isLoggedIn(): boolean;
}