import React from 'react';
import {Navbar} from "@/components/navbar.tsx";
import {GetUserResponse} from "@/models/services/responses/GetUserResponse.ts";

interface LayoutProps {
    user: GetUserResponse;
    left: React.ReactNode;
    right: React.ReactNode;
    leftWeight?: number;
    rightWeight?: number;
}

const Layout: React.FC<LayoutProps> = ({
                                           user,
                                           left,
                                           right,
                                           leftWeight = 1,
                                           rightWeight = 1
                                       }) => {
    return (
        <div className="flex flex-col h-screen">
            <div className="flex-none">
                <Navbar user={user}/>
            </div>
            <div className="flex flex-1 overflow-hidden">
                <div
                    className="overflow-auto"
                    style={{flex: leftWeight.toString()}}
                >
                    {left}
                </div>
                <div
                    className="overflow-auto"
                    style={{flex: rightWeight.toString()}}
                >
                    {right}
                </div>
            </div>
        </div>
    );
};

export default Layout;