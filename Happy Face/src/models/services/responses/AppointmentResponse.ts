import {Patient} from "@/models";
import {Doctor} from "@/models/Doctor.ts";
import {AppointmentType} from "@/models/enums/AppointmentType.ts";

export interface AppointmentResponse {
    id: number;
    title: string;
    start: string;
    end: string;
    appointmentType: AppointmentType;
    doctor: Doctor;
    patient: Patient;
}
