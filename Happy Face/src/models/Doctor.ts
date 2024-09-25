import {Role} from "@/models/enums/Role.ts";

export interface Doctor {
    id: number;
    username: string;
    password: string;
    name: string;
    email: string;
    role: Role.DOCTOR;
    active: boolean;
}