
import axios, { AxiosInstance } from 'axios';
import { AuthService } from '@/services/authService.ts';

export const API_URL: string = 'http://localhost:8080';

function createApi(useInterceptor: boolean = true) {
    const api: AxiosInstance = axios.create({
        baseURL: API_URL,
        headers: {
            'Content-Type': 'application/json',
        },
    });
    if (useInterceptor) {
        api.interceptors.request.use(async (config) => {
            const authService = new AuthService();
            const token = await authService.getDecryptedToken();
            if (token) {
                config.headers.Authorization = `Bearer ${token}`;
            }
            return config;
        });
    }
    return api;
}

export const api = createApi();

export const apiWithoutAuth = createApi(false);