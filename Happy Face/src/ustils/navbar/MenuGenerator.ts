// src/utils/navbar/MenuGenerator.ts
import { Menu } from "@/models/navbar/Menu.ts";

//Placeholder functions for menu items

const handleNewAppointment = () => {
    console.log("New appointment clicked");
};

const handleViewAppointments = () => {
    console.log("View all appointments clicked");
};

const handleDeleteAppointment = () => {
    console.log("Delete appointment clicked");
};

//Generate menus based on user role
type MenuGeneratorType = (role: string) => Menu[];
export const MenuGenerator: MenuGeneratorType = (role: string): Menu[] => {
    switch (role) {
        case 'RECEPTIONIST':
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
                    name: "Patient",
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
        case 'DOCTOR':
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
        case 'PATIENT':
            return [
                {
                    name: "Appointment",
                    items: [
                        { label: "View All Appointments", shortcut: "⌘L", onClick: handleViewAppointments },
                    ],
                },
                {
                    name: "Billing & Payments",
                    items: [
                        { label: "View Payment Status", shortcut: "⌘Y" },
                    ],
                },
            ];
        default:
            return [];
    }
};