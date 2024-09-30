import {AppointmentRequest, Doctor} from "@/models";
import {AppointmentResponse} from "@/models/services/responses/AppointmentResponse.ts";
import {AppointmentUpdateRequest} from "@/models/services/requests/AppointmentUpdateRequest.ts";
import {AppointmentType} from "@/models/enums/AppointmentType.ts";

export interface IAppointmentService {
    getFreeSlots: (doctorId: number, date: string, appointmentType: AppointmentType, weekView?: boolean) => Promise<string[]>;
    createAppointment: (appointmentRequest: AppointmentRequest) => Promise<AppointmentResponse>;
    getAppointment: (appointmentId: number) => Promise<AppointmentResponse>;
    updateAppointment: (appointmentRequest: AppointmentUpdateRequest) => Promise<AppointmentResponse>;
    getAppointmentsForDoctor: (doctorId: number, date: string, weekView?: boolean) => Promise<AppointmentResponse[]>;
    deleteAppointment: (appointmentId: number) => Promise<void>;
    fetchDoctors: () => Promise<Doctor[]>;
}