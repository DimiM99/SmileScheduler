import {Role} from "@/models/enums/Role.ts";

export interface User {
    id: string;
    username: string;
    password: string;
    role: Role;
}
