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
import {MenuGenerator} from "@/helpers/navbar/MenuGenerator.ts";
import {Menu} from "@/models/navbar/Menu.ts";


interface NavbarProps {
    user: User;
}

// Dynamic Navbar Component
export const Navbar: React.FC<NavbarProps> = ({ user }) => {

    const menus: Menu[] = MenuGenerator(user.role);

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
