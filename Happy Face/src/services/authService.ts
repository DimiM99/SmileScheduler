import { AxiosResponse } from 'axios';
import { api } from './apiConfig';
import { handleApiError } from './errorHandler';
import {LoginResponse} from "@/models/services/responses/LoginResponse.ts";
import {GetUserResponse} from "@/models/services/responses/GetUserResponse.ts";
import * from 'crypto-js';
import { LoginRequest } from '@/models/services/responses/LoginRequest.ts';
import {IAuthService} from "@/models/services/IAuthService.ts";


export class AuthService implements IAuthService {

    async login(req: LoginRequest): Promise<LoginResponse> {
        try {

            console.log(req)
            const encryptedCredentials = CryptoJS.AES.encrypt(JSON.stringify(req), process.env.REACT_APP_CRYPTO_SECRET);
            sessionStorage.setItem('credentials', encryptedCredentials.toString());
            console.log('ha')
            const response: AxiosResponse<LoginResponse> = await api.post('/auth/login', req);
            this.saveToken(response.data);
            console.log(response)
            return response.data;
        } catch (error) {
            console.log(error)
            throw handleApiError(error);
        }
    }

    async getUser(): Promise<GetUserResponse> {
        try {
            const token = this.getDecryptedToken();
            if (!token) throw new Error('Token not found');
            const response: AxiosResponse<GetUserResponse> = await api.get('/user', {
                headers: { Authorization: `Bearer ${token}` },
            });
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    }

    private saveToken = (loginResponse: LoginResponse): void => {
        const encryptedToken = CryptoJS.AES.encrypt(loginResponse.token, process.env.REACT_APP_CRYPTO_SECRET);
        sessionStorage.setItem('token', encryptedToken.toString());
        const expiryDate = new Date().getTime() + loginResponse.expiresIn;
        localStorage.setItem('tokenExpiry', expiryDate.toString());
    }

    clearToken() {
        sessionStorage.removeItem('token');
        sessionStorage.removeItem('tokenExpiry');
    }

    refreshToken = async (): Promise<void> => {
        try {
            const credentials = sessionStorage.getItem('credentials');
            if (!credentials) return;

            const decryptedCredentials = CryptoJS.AES.decrypt(credentials, process.env.REACT_APP_CRYPTO_SECRET);
            const parsedCredentials: LoginRequest = JSON.parse(decryptedCredentials.toString(CryptoJS.enc.Utf8)) as LoginRequest;


            const loginResponse = await this.login(parsedCredentials);
            this.clearToken();
            this.saveToken(loginResponse);
        } catch (error) {
            console.error('Token refresh failed', error);
        }
    }

    getDecryptedToken = async (): Promise<string | null> => {
        const encryptedToken = sessionStorage.getItem('token');
        const expiryDateString = localStorage.getItem('tokenExpiry');
        if (!encryptedToken || !expiryDateString) return null;

        const decryptedTokenBytes = CryptoJS.AES.decrypt(encryptedToken, process.env.REACT_APP_CRYPTO_SECRET);
        const decryptedToken = decryptedTokenBytes.toString(CryptoJS.enc.Utf8);

        const expiryDate = parseInt(expiryDateString);
        if (new Date().getTime() >= expiryDate) {
            await this.refreshToken();
            return await this.getDecryptedToken();  // Ensure the function returns the refreshed token
        }
        return decryptedToken as string;

    }

    isLoggedIn(): boolean {
        const encryptedToken = sessionStorage.getItem('token');
        const expiryDateString = localStorage.getItem('tokenExpiry');
        if (!encryptedToken || !expiryDateString) return false;

        const expiryDate = parseInt(expiryDateString);
        return new Date().getTime() < expiryDate;
    }

}