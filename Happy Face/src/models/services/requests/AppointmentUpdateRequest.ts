import {AppointmentType} from "@/models/enums/AppointmentType.ts";

export interface AppointmentUpdateRequest {
    id: number
    title: string;
    patientId: number;
    doctorId: number;
    start: string;
    appointmentType: AppointmentType;
    notes: string;
    reasonForAppointment: string;
}