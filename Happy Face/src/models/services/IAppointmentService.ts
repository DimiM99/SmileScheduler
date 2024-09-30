import {AppointmentRequest, Doctor} from "@/models";
import {AppointmentResponse} from "@/models/services/responses/AppointmentResponse.ts";
import {AppointmentUpdateRequest} from "@/models/services/requests/AppointmentUpdateRequest.ts";

export interface IAppointmentService {
    createAppointment: (appointmentRequest: AppointmentRequest) => Promise<AppointmentResponse>;
    getAppointment: (appointmentId: number) => Promise<AppointmentResponse>;
    updateAppointment: (appointmentRequest: AppointmentUpdateRequest) => Promise<AppointmentResponse>;
    deleteAppointment: (appointmentId: number) => Promise<void>;
    fetchDoctors: () => Promise<Doctor[]>;
}