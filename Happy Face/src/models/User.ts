import {Role} from "@/models/enums/Role.ts";
export interface User {
    id: number,
    username: string;
    password: string;
    name: string;
    email: string;
    role: Role;
}
