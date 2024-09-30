
import {Menu} from "@/models/components/navbar/Menu.ts";
import {Role} from "@/models/enums/Role.ts";
import { toast } from "sonner"

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
                    name: "My Schedule",
                    items: [
                        {
                            label: "Manage Availability",
                            shortcut: "⌘A",
                            onClick: () => {
                                toast("Sorry", {
                                    description: "The availability is not editable currently.",
                                    action: {
                                        label: "Ok",
                                        onClick: () => {
                                            console.log("Undo");
                                        },
                                    }
                                })
                            }
                        },

                        {
                            label: "Plan Vacation",
                            shortcut: "⌘V",
                            onClick: () => {
                                toast("Sorry", {
                                    description: "The vacation planner is not available currently.",
                                    action: {
                                        label: "Ok",
                                        onClick: () => {
                                            console.log("Undo");
                                        },
                                    }
                                })
                            }
                        },
                    ],
                },
            ];
        case Role.ADMIN:
            return [
                {
                    name: "Clinic Management",
                    items: [
                        {
                            label: "Edit Office Hours",
                            shortcut: "⌘O" ,
                            onClick: () => {
                                toast("Sorry", {
                                      description: "The Office Hours are not editable currently.",
                                      action: {
                                        label: "Ok",
                                        onClick: () => { console.log("Undo"); },
                                      },
                                })
                            }
                        },
                    ],
                }
            ];
        default:
            return [];
    }
};
