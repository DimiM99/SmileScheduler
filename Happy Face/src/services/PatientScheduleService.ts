import { IPatientScheduleService } from "@/models/services/IPatientScheduleService.ts";
import { AppointmentResponse } from "@/models/services/responses/AppointmentResponse";
import { apiWithoutAuth } from "@/services/apiConfig.ts";
import CryptoJS from 'crypto-js';

export class PatientScheduleService implements IPatientScheduleService {

    private readonly secretKey: string = 'your-secret-key-here';
    private readonly message: string = 'your-message-here';

    generateHmacSha256(message: string, key: string): string {
        const hash = CryptoJS.HmacSHA256(message, key);
        return hash.toString(CryptoJS.enc.Base64);
    }

    async fetchAppointments(dateOfBirth: Date, patientID: number): Promise<AppointmentResponse[]> {
        try {
            const formattedDateOfBirth = dateOfBirth.toISOString().split('T')[0];
            const receivedHash = this.generateHmacSha256(this.message, this.secretKey);

            const requestBody = {
                receivedHash,
                patientId: patientID,
                patientDateOfBirth: formattedDateOfBirth
            };

            const response = await apiWithoutAuth.post<AppointmentResponse[]>('/api/patient-schedule', requestBody);

            return response.data;
        } catch (error) {
            console.error('Error fetching appointments:', error);
            throw error;
        }
    }
}