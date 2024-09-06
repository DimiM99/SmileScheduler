import React from 'react';

interface LayoutProps {
    top: React.ReactNode;
    left: React.ReactNode;
    right: React.ReactNode;
    leftWeight?: number;
    rightWeight?: number;
}

const Layout: React.FC<LayoutProps> = ({
                                           top,
                                           left,
                                           right,
                                           leftWeight = 1,
                                           rightWeight = 1
                                       }) => {
    return (
        <div className="flex flex-col h-screen">
            <div className="flex-none">
                {top}
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