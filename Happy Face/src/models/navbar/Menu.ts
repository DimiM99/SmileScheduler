import {Role} from "@/models/enums/Role.ts";

export interface MenuItem {
    label: string;
    shortcut?: string;
    onClick?: () => void;
}

export interface Menu {
    name: Role;
    items: (MenuItem | "separator")[];
}

