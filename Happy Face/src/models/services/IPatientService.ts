import {Patient} from "@/models";

export interface IPatientService {
    getPatientByInsuranceNumber(numner: number): Promise<Patient>;
}