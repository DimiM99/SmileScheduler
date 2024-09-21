import {Role} from "@/models/enums/Role.ts";
export interface User {
    username: string;
    password: string;
    name: string;
    email: string;
    role: Role;
}
