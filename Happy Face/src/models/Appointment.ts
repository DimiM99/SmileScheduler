export interface Appointment {
  id: string;
  patientId: string;
  doctorId: string;
  appointmentTypeId: string;
  date: Date;
  time: string;
}