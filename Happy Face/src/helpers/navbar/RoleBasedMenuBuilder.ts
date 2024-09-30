import {Menu} from "@/models/components/navbar/Menu.ts";
import {Role} from "@/models/enums/Role.ts";
import {toast} from "sonner"

// Placeholder functions for menu items

const handleNewAppointment = () => {
    toast("Sorry", {
        description: "Not available currently.",
        action: {
            label: "Ok",
            onClick: () => {
                console.log("Undo");
            },
        }
    })
};

const handleViewAppointments = () => {
    toast("Sorry", {
        description: "Not available currently.",
        action: {
            label: "Ok",
            onClick: () => {
                console.log("Undo");
            },
        }
    })
};

const handleDeleteAppointment = () => {
    toast("Sorry", {
        description: "Not available currently.",
        action: {
            label: "Ok",
            onClick: () => {
                console.log("Undo");
            },
        }
    })
};


type MenuGeneratorType = (role: Role) => Menu[];

export const RoleBasedMenuBuilder: MenuGeneratorType = (role: Role): Menu[] => {


    switch (role) { // Ensure role comparison is case-insensitive
        case Role.RECEPTIONIST:
            return [
                {
                    name: "Appointment",
                    items: [
                        {label: "New Appointment", shortcut: "⌘N", onClick: handleNewAppointment},
                        {label: "Edit Appointment", shortcut: "⌘E",
                            onClick: () => {
                                toast("Sorry", {
                                    description: "The appointment is not editable currently.",
                                    action: {
                                        label: "Ok",
                                        onClick: () => {
                                            console.log("Undo");
                                        },
                                    }
                                })
                            }
                        },
                        {label: "View All Appointments", shortcut: "⌘L", onClick: handleViewAppointments},
                        'separator',
                        {label: "Reschedule Appointment", shortcut: "⌘R",
                            onClick: () => {
                                toast("Sorry", {
                                    description: "The appointment is not reschedulable currently.",
                                    action: {
                                        label: "Ok",
                                        onClick: () => {
                                            console.log("Undo");
                                        },
                                    }
                                })
                            }
                        },
                        {label: "Delete Appointment", shortcut: "⌘D", onClick: handleDeleteAppointment},
                    ],
                },
                {
                    name: 'Patient',
                    items: [
                        {label: "Add New Patient", shortcut: "⌘P",
                            onClick: () => {
                                toast("Sorry", {
                                    description: "The patient is not addable currently.",
                                    action: {
                                        label: "Ok",
                                        onClick: () => {
                                            console.log("Undo");
                                        },
                                    }
                                })
                            }
                        },
                        {label: "View Patient List", shortcut: "⌘V",
                            onClick: () => {
                                toast("Sorry", {
                                    description: "The patient list is not viewable currently.",
                                    action: {
                                        label: "Ok",
                                        onClick: () => {
                                            console.log("Undo");
                                        },
                                    }
                                })
                            }
                        },
                        'separator',
                        {label: "View Patient Profile", shortcut: "⌘O",
                            onClick: () => {
                                toast("Sorry", {
                                    description: "The patient profile is not viewable currently.",
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
                            shortcut: "⌘O",
                            onClick: () => {
                                toast("Sorry", {
                                    description: "The Office Hours are not editable currently.",
                                    action: {
                                        label: "Ok",
                                        onClick: () => {
                                            console.log("Undo");
                                        },
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
