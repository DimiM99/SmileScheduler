import { AxiosResponse } from 'axios';
import { apiWithoutAuth } from './apiConfig';
import { handleApiError } from './errorHandler';
import {LoginResponse} from "@/models/services/responses/LoginResponse";
import {GetUserResponse} from "@/models/services/responses/GetUserResponse";
import CryptoJS from 'crypto-js';
import { LoginRequest } from '@/models/services/requests/LoginRequest';
import {IAuthService} from "@/models/services/IAuthService";



export class AuthService implements IAuthService {

    private secret = import.meta.env.VITE_CRYPTO_SECRET || "default";

    async login(req: LoginRequest): Promise<LoginResponse> {
        try {
            const encryptedCredentials = CryptoJS.AES.encrypt(JSON.stringify(req), this.secret);
            sessionStorage.setItem('credentials', encryptedCredentials.toString());
            const response: AxiosResponse<LoginResponse> = await apiWithoutAuth.post('/auth/login', req);
            this.saveToken(response.data);
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    }

    async getUser(): Promise<GetUserResponse> {
        try {
            const token = await this.getDecryptedToken();
            const response: AxiosResponse<GetUserResponse> = await apiWithoutAuth.get('/user', {
                headers: { Authorization: `Bearer ${token}` },
            });
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    }

    saveToken = (loginResponse: LoginResponse): void => {
        const encryptedToken = CryptoJS.AES.encrypt(loginResponse.token, this.secret);
        sessionStorage.setItem('token', encryptedToken.toString());
        const expiryDate = new Date().getTime() + loginResponse.expiresIn;
        sessionStorage.setItem('tokenExpiry', expiryDate.toString());
    }

    clearToken() {
        sessionStorage.removeItem('token');
        sessionStorage.removeItem('tokenExpiry');
        sessionStorage.removeItem('credentials');
    }

    refreshToken = async (): Promise<void> => {
        try {
            const credentials = sessionStorage.getItem('credentials');
            if (!credentials) return;

            const decryptedCredentials = CryptoJS.AES.decrypt(credentials, this.secret);
            const parsedCredentials: LoginRequest = JSON.parse(decryptedCredentials.toString(CryptoJS.enc.Utf8)) as LoginRequest;


            const loginResponse = await this.login(parsedCredentials);
            this.clearToken();
            this.saveToken(loginResponse);
        } catch (error) {
            console.error('Token refresh failed', error);
        }
    }

    getDecryptedToken = async (): Promise<string> => {
        const encryptedToken = sessionStorage.getItem('token');
        const expiryDateString = sessionStorage.getItem('tokenExpiry');
        if (!encryptedToken || !expiryDateString) throw new Error('Token not found');

        const decryptedTokenBytes = CryptoJS.AES.decrypt(encryptedToken, this.secret);
        const decryptedToken = decryptedTokenBytes.toString(CryptoJS.enc.Utf8);

        const expiryDate = parseInt(expiryDateString);
        if (new Date().getTime() >= expiryDate) {
            await this.refreshToken();
            return await this.getDecryptedToken();
        }
        return decryptedToken;

    }

    isLoggedIn(): boolean {
        const encryptedToken = sessionStorage.getItem('token');
        const expiryDateString = sessionStorage.getItem('tokenExpiry');
        if (!encryptedToken || !expiryDateString) return false;

        const expiryDate = parseInt(expiryDateString);
        return new Date().getTime() < expiryDate;
    }

}