import {Patient} from "@/models";

export interface IPatientService {
    getPatientByInsuranceNumber(numner: string): Promise<Patient>;
}