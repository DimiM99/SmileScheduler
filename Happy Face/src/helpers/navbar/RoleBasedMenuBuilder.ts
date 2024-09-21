
import {Menu} from "@/models/components/navbar/Menu.ts";
import {Role} from "@/models/enums/Role.ts";

// Placeholder functions for menu items

const handleNewAppointment = () => {
    console.log("New appointment clicked");
};

const handleViewAppointments = () => {
    console.log("View all appointments clicked");
};

const handleDeleteAppointment = () => {
    console.log("Delete appointment clicked");
};

// New handlers for ADMIN
const handleRegisterUser = () => {
    console.log("Register user clicked");
};

const handleManageUsers = () => {
    console.log("Manage users clicked");
};

const handleViewSystemLogs = () => {
    console.log("View system logs clicked");
};

const handleSystemSettings = () => {
    console.log("System settings clicked");
};


type MenuGeneratorType = (role: Role) => Menu[];

export const RoleBasedMenuBuilder: MenuGeneratorType = (role: Role): Menu[] => {
    switch (role) { // Ensure role comparison is case-insensitive
        case Role.RECEPTIONIST:
            return [
                {
                    name: "Appointment",
                    items: [
                        { label: "New Appointment", shortcut: "⌘N", onClick: handleNewAppointment },
                        { label: "Edit Appointment", shortcut: "⌘E" },
                        { label: "View All Appointments", shortcut: "⌘L", onClick: handleViewAppointments },
                        'separator',
                        { label: "Reschedule Appointment", shortcut: "⌘R" },
                        { label: "Delete Appointment", shortcut: "⌘D", onClick: handleDeleteAppointment },
                    ],
                },
                {
                    name: 'Patient',
                    items: [
                        { label: "Add New Patient", shortcut: "⌘P" },
                        { label: "View Patient List", shortcut: "⌘V" },
                        'separator',
                        { label: "View Patient Profile", shortcut: "⌘O" },
                    ],
                },
                {
                    name: "Doctor Schedules",
                    items: [
                        { label: "View Doctor Availability", shortcut: "⌘A" },
                        { label: "Block Doctor Time", shortcut: "⌘T" },
                    ],
                },
                {
                    name: "Billing & Payments",
                    items: [
                        { label: "Generate Invoice", shortcut: "⌘I" },
                        { label: "View Payment Status", shortcut: "⌘Y" },
                        { label: "View Payment History", shortcut: "⌘H" },
                    ],
                },
            ];
        case Role.DOCTOR:
            return [
                {
                    name: "Appointment",
                    items: [
                        { label: "View All Appointments", shortcut: "⌘L", onClick: handleViewAppointments },
                        'separator',
                        { label: "Reschedule Appointment", shortcut: "⌘R" },
                    ],
                },
                {
                    name: "Doctor Schedules",
                    items: [
                        { label: "View Doctor Availability", shortcut: "⌘A" },
                    ],
                },
            ];
        case Role.ADMIN:
            return [
                {
                    name: "User Management",
                    items: [
                        { label: "Register New User", shortcut: "⌘U", onClick: handleRegisterUser },
                        { label: "Manage Users", shortcut: "⌘M", onClick: handleManageUsers },
                        'separator',
                        { label: "View System Logs", shortcut: "⌘S", onClick: handleViewSystemLogs },
                        { label: "System Settings", shortcut: "⌘T", onClick: handleSystemSettings },
                    ],
                },
                {
                    name: "Dashboard",
                    items: [
                        { label: "View Analytics", shortcut: "⌘A" },
                        { label: "System Health", shortcut: "⌘H" },
                    ],
                },
                {
                    name: "Reports",
                    items: [
                        { label: "Generate Reports", shortcut: "⌘G" },
                        { label: "Export Data", shortcut: "⌘E" },
                    ],
                },
            ];
        default:
            return [];
    }
};
