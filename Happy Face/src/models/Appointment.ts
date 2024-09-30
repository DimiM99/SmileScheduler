import {AppointmentType} from "@/models/enums/AppointmentType.ts";
import {Doctor} from "@/models/Doctor.ts";
import {Patient} from "@/models/Patient.ts";

export interface Appointment {
    id: number;
    title: string;
    start: string;
    end: string;
    appointmentType: AppointmentType;
    reasonForAppointment: string;
    notes: string;
    doctor: Doctor;
    patient: Patient;
}