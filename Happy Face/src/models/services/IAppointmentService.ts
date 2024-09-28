import {AppointmentRequest} from "@/models";
import {AppointmentResponse} from "@/models/services/responses/AppointmentResponse.ts";

export interface IAppointmentService {
    createAppointment: (appointmentRequest: AppointmentRequest) => Promise<AppointmentResponse>;
    getAppointment: (appointmentId: number) => Promise<AppointmentResponse>;
    updateAppointment: (appointmentRequest: AppointmentRequest) => Promise<AppointmentResponse>;
    deleteAppointment: (appointmentId: number) => Promise<void>;
}