import {AppointmentResponse} from "@/models/services/responses/AppointmentResponse.ts";

export interface IPatientScheduleService {
    fetchAppointments: (dateOfBirth: Date, patientID: number) => Promise<AppointmentResponse[]>
}