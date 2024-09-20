import axios, { AxiosInstance } from 'axios';

export const API_URL: string = 'http://localhost:8080';

export const api: AxiosInstance = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});