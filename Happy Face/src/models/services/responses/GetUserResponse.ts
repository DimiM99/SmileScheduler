import {Role} from "@/models/enums/Role.ts";

export interface GetUserResponse {
    id: number;
    username: string;
    password: null;
    name: string;
    email: string;
    role: Role;
    active: boolean;
}