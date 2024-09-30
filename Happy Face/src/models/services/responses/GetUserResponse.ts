import {Role} from "@/models/enums/Role.ts";

export interface GetUserResponse {
    id: number;
    username: string;
    password: string;
    name: string;
    email: string;
    role: Role;
    active: boolean;
}