export interface MenuItem {
    label: string;
    shortcut?: string;
    onClick?: () => void;
}

export interface Menu {
    name: string;
    items: (MenuItem | "separator")[];
}
