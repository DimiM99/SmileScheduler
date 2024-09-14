import axios, { AxiosError } from 'axios';

class ApiError extends Error {
    constructor(public status: number, message: string) {
        super(message);
        this.name = 'ApiError';
    }
}

export const handleApiError = (error: unknown): Error => {
    if (axios.isAxiosError(error)) {
        const axiosError = error as AxiosError<string>;
        if (axiosError.response) {
            throw new ApiError(axiosError.response.status, axiosError.response.data || 'An error occurred');
        }
    }
    throw new ApiError(500, 'An unexpected error occurred');
};