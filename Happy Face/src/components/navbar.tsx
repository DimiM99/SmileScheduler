import React from 'react';
import {
    Menubar,
    MenubarContent,
    MenubarItem,
    MenubarMenu,
    MenubarSeparator,
    MenubarShortcut,
    MenubarTrigger,
} from "@/components/ui/menubar";

import {
    Avatar,
    AvatarFallback,
    AvatarImage,
} from "@/components/ui/avatar"
import {User} from "@/models";
import {Menu} from "@/models/Menu.ts";




//const handleNewAppointment = () => {
//    console.log("New appointment clicked");
//};
//
//const handleViewAppointments = () => {
//    console.log("View all appointments clicked");
//};
//
//const handleDeleteAppointment = () => {
//    console.log("Delete appointment clicked");
//};
//
//const menus: Menu[] = [
//    {
//        name: "Appointment",
//        items: [
//            { label: "New Appointment", shortcut: "⌘N", onClick: handleNewAppointment },
//            { label: "Edit Appointment", shortcut: "⌘E" },
//            { label: "View All Appointments", shortcut: "⌘L", onClick: handleViewAppointments },
//            'separator',
//            { label: "Reschedule Appointment", shortcut: "⌘R" },
//            { label: "Delete Appointment", shortcut: "⌘D", onClick: handleDeleteAppointment },
//        ],
//    },
//    {
//        name: "Patient",
//        items: [
//            { label: "Add New Patient", shortcut: "⌘P" },
//            { label: "View Patient List", shortcut: "⌘V" },
//            'separator',
//            { label: "View Patient Profile", shortcut: "⌘O" },
//        ],
//    },
//    {
//        name: "Doctor Schedules",
//        items: [
//            { label: "View Doctor Availability", shortcut: "⌘A" },
//            { label: "Block Doctor Time", shortcut: "⌘T" },
//        ],
//    },
//    {
//        name: "Billing & Payments",
//        items: [
//            { label: "Generate Invoice", shortcut: "⌘I" },
//            { label: "View Payment Status", shortcut: "⌘Y" },
//            { label: "View Payment History", shortcut: "⌘H" },
//        ],
//    },
//];




// Props for the Navbar
interface NavbarProps {
    menus: Menu[];
    user: User;
}

// Dynamic Navbar Component
export const Navbar: React.FC<NavbarProps> = ({ menus, user }) => {

    return (
        <div className="flex justify-between items-center w-full p-4">
            {/* Dynamically render each menu on the left */}
            <Menubar className="flex space-x-4">
                {menus.map((menu, menuIndex) => (
                    <MenubarMenu key={menuIndex}>
                        <MenubarTrigger>{menu.name}</MenubarTrigger>
                        <MenubarContent>
                            {menu.items.map((item, itemIndex) => {
                                // Check if item is a separator
                                if (item === 'separator') {
                                    return <MenubarSeparator key={itemIndex} />;
                                }

                                // Otherwise, render the item with optional shortcut
                                return (
                                    <MenubarItem key={itemIndex} onClick={item.onClick}>
                                        {item.label}
                                        {item.shortcut && (
                                            <MenubarShortcut>{item.shortcut}</MenubarShortcut>
                                        )}
                                    </MenubarItem>
                                );
                            })}
                        </MenubarContent>
                    </MenubarMenu>
                ))}
            </Menubar>

            {/* Right-aligned profile section */}
            <div className="ml-auto pr-4">
                <Menubar className="border-none shadow-none">
                    <MenubarMenu>
                        <MenubarTrigger>
                            <Avatar>
                                <AvatarImage src="https://github.com/shadcn.png" alt="@shadcn" />
                                <AvatarFallback>{user.username}</AvatarFallback>
                            </Avatar>
                        </MenubarTrigger>
                        <MenubarContent>
                            <MenubarItem >Account Settings</MenubarItem>

                            <MenubarItem>Logout</MenubarItem>
                        </MenubarContent>
                    </MenubarMenu>
                </Menubar>
            </div>
        </div>
    );
};
