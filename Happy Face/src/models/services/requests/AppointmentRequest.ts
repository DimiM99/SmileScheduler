import {AppointmentType} from "@/models/enums/AppointmentType.ts";
import {Patient} from "@/models";


export interface AppointmentRequest {
    title: string;
    doctorId: number;
    start: Date;
    appointmentType: AppointmentType;
    patient: Patient;
}