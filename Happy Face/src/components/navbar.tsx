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
import {RoleBasedMenuBuilder} from "@/helpers/navbar/RoleBasedMenuBuilder.ts";
import {Menu} from "@/models/components/navbar/Menu.ts";
import {useAuth} from "@/hooks/useAuth.ts";
import {GetUserResponse} from "@/models/services/responses/GetUserResponse.ts";


interface NavbarProps {
    user: GetUserResponse;
}

// Dynamic Navbar Component
export const Navbar: React.FC<NavbarProps> = ({ user }) => {
    const menus: Menu[] = RoleBasedMenuBuilder(user.role);
    const {logout} = useAuth();

    function handleLogout() {
        logout();
    }

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

                            <MenubarItem onClick={handleLogout}>Logout</MenubarItem>
                        </MenubarContent>
                    </MenubarMenu>
                </Menubar>
            </div>
        </div>
    );
};
