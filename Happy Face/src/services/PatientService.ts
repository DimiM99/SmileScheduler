import {IPatientService} from "@/models/services/IPatientService.ts";
import {api} from "@/services/apiConfig.ts";
import {Patient} from "@/models";
import {handleApiError} from "@/services/errorHandler.ts";
import {AxiosResponse} from "axios";

export class PatientService implements IPatientService {

    public async getPatientByInsuranceNumber(number: number): Promise<Patient> {
        try {
            const response: AxiosResponse<Patient> = await api.get(`/api/patients/search?insuranceNumber=${number.toString()}`);
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    }

}