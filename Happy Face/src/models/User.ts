import {Role} from "@/models/enums/Role.ts";
import {EmailAddress} from "@/classes/EmailAdress.ts";
export interface User {
    username: string;
    password: string;
    name: string;
    email: EmailAddress;
    role: Role;
}
