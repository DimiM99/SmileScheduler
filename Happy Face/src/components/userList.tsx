import React, { useState } from "react";
import { User } from "@/models/User";
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table";
import {
    Card,
    CardContent, CardFooter,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";

interface UserListProps {
    users: User[];
    selectedUser: User | null;
    onUserSelect: (user: User | null) => void;
}

export const UserList: React.FC<UserListProps> = ({users, selectedUser, onUserSelect}) => {

    const [currentPage, setCurrentPage] = useState(1);

    const itemsPerPage = 10;

    const totalPages = Math.ceil(users.length / itemsPerPage);
    const startIndex = (currentPage - 1) * itemsPerPage
    const endIndex = startIndex + itemsPerPage;
    const currentPageUsers = users.slice(startIndex, endIndex);

    const handleRowClick = (user: User) => {
        if (selectedUser?.username === user.username) {
            onUserSelect(null);
        } else {
            onUserSelect(user);
        }
    };

    return (
        <Card className="w-full h-4/5 max-w-5xl p-4 shadow-md">
            <CardHeader>
                <CardTitle className="text-center">Registered Users</CardTitle>
            </CardHeader>

            <CardContent className="h-4/5">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>Username</TableHead>
                            <TableHead>Name</TableHead>
                            <TableHead>Email</TableHead>
                            <TableHead>Role</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {currentPageUsers.map((user) => (
                            <TableRow
                                key={user.id}
                                onClick={() => {
                                    handleRowClick(user);
                                }}
                                className={
                                    selectedUser?.username === user.username
                                        ? "bg-gray-100"
                                        : ""
                                }
                                style={{cursor: "pointer"}}
                            >
                                <TableCell>{user.username}</TableCell>
                                <TableCell>{user.name}</TableCell>
                                <TableCell>{user.email}</TableCell>
                                <TableCell>{user.role}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </CardContent>
            <CardFooter className="flex justify-between">
                <div className="text-sm test-muted-foregroud">
                    Page {currentPage} of {totalPages}
                </div>
                <div className="space-x-2">
                    <Button
                        size="sm"
                        onClick={() => {
                            setCurrentPage((prev) => Math.max(prev - 1, 1));
                        }}
                        disabled={currentPage === 1}
                    >
                        Previous
                    </Button>
                    <Button
                        size="sm"
                        onClick={() => {
                            setCurrentPage((prev) => Math.max(prev + 1, totalPages));
                        }}
                        disabled={currentPage === totalPages}
                    >
                        Next
                    </Button>
                </div>
            </CardFooter>
        </Card>
    );
}
