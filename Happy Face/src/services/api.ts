import axios, {AxiosError, AxiosInstance, AxiosResponse} from 'axios';
import {Patient, AppointmentType, Appointment, Doctor, LoginResponse} from '@/models';

const API_URL: string = 'https://your-api-url.com';

class ApiError extends Error {
    constructor(public status: number, message: string) {
        super (message);
        this.name = 'ApiError';
    }
}

const api: AxiosInstance = axios.create ({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

const handleApiError = (error: unknown): Error => {
    if (axios.isAxiosError (error)) {
        const axiosError = error as AxiosError<string>;
        if (axiosError.response) {
            throw new ApiError (axiosError.response.status, axiosError.response.data || 'An error occurred');
        }
    }
    throw new ApiError (500, 'An unexpected error occurred');
};

export const login = async (username: string, password: string): Promise<LoginResponse> => {
    try {
        const response: AxiosResponse<LoginResponse> = await api.post ('/login', {username, password});
        return response.data;
    } catch (error) {
        throw handleApiError (error);
    }
};

export const fetchAppointments = async (token: string): Promise<Appointment[]> => {
    try {
        const response: AxiosResponse<Appointment[]> = await api.get ('/appointments', {
            headers: {Authorization: `Bearer ${token}`},
        });
        return response.data;
    } catch (error) {
        throw handleApiError (error);
    }
};

export const createAppointment = async (token: string, appointmentData: Omit<Appointment, 'id'>): Promise<Appointment> => {
    try {
        const response: AxiosResponse<Appointment> = await api.post ('/appointments', appointmentData, {
            headers: {Authorization: `Bearer ${token}`},
        });
        return response.data;
    } catch (error) {
        throw handleApiError (error);
    }
};

export const fetchPatients = async (token: string): Promise<Patient[]> => {
    try {
        const response: AxiosResponse<Patient[]> = await api.get ('/patients', {
            headers: {Authorization: `Bearer ${token}`},
        });
        return response.data;
    } catch (error) {
        throw handleApiError (error);
    }
};

export const fetchDoctors = async (token: string): Promise<Doctor[]> => {
    try {
        const response: AxiosResponse<Doctor[]> = await api.get ('/doctors', {
            headers: {Authorization: `Bearer ${token}`},
        });
        return response.data;
    } catch (error) {
        throw handleApiError (error);
    }
};

export const fetchAppointmentTypes = async (token: string): Promise<AppointmentType[]> => {
    try {
        const response: AxiosResponse<AppointmentType[]> = await api.get ('/appointment-types', {
            headers: {Authorization: `Bearer ${token}`},
        });
        return response.data;
    } catch (error) {
        throw handleApiError (error);
    }
};

// Add more API functions as needed
