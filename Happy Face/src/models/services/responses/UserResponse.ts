import {Role} from "@/models/enums/Role.ts";


export interface UserResponse {
    id: number,
    username: string;
    password?: string;
    name: string;
    email: string;
    role: Role;
    active: boolean;
}