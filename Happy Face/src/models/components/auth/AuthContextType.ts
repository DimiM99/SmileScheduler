
import {LoginRequest} from "@/models/services/requests/LoginRequest.ts";
import {GetUserResponse} from "@/models/services/responses/GetUserResponse.ts";

export interface AuthContextType {
    user: GetUserResponse | null;
    login: (loginRequest: LoginRequest) => Promise<void>;
    logout: () => void;
}