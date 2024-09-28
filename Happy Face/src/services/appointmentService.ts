import { AxiosResponse } from 'axios';
import { api } from './apiConfig';
import { AppointmentRequest } from '@/models/services/requests/AppointmentRequest';
import { AppointmentResponse } from '@/models/services/responses/AppointmentResponse';
import { IAppointmentService } from '@/models/services/IAppointmentService';
import { handleApiError } from './errorHandler';

export class AppointmentService implements IAppointmentService {


    async createAppointment(appointmentRequest: AppointmentRequest): Promise<AppointmentResponse> {
        try {
            const response: AxiosResponse<AppointmentResponse> = await api.post('/api/appointments', appointmentRequest);
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    }

    async getAppointment(appointmentId: number): Promise<AppointmentResponse> {
        try {
            const response: AxiosResponse<AppointmentResponse> = await api.get(`/api/appointments?appointmentId=${appointmentId.toString()}`);
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    }

    async updateAppointment(appointmentRequest: AppointmentRequest): Promise<AppointmentResponse> {
        try {
            const response: AxiosResponse<AppointmentResponse> = await api.put(`/api/appointments/`, appointmentRequest);
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    }

    async deleteAppointment(appointmentId: number): Promise<void> {
        try {
            await api.delete(`/api/appointments/${appointmentId.toString()}`);
        } catch (error) {
            throw handleApiError(error);
        }
    }
}
