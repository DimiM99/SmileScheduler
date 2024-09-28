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
    CardContent,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button"; // Ensure you have a Button component

interface UserListProps {
    users: User[];
    selectedUser: User | null;
    onUserSelect: (user: User | null) => void;
}

export const UserList: React.FC<UserListProps> = ({
                                                      users,
                                                      selectedUser,
                                                      onUserSelect,
                                                  }) => {
    const [page, setPage] = useState(0);
    const itemsPerPage = 10;

    const handleRowClick = (user: User) => {
        if (selectedUser?.username === user.username) {
            onUserSelect(null);
        } else {
            onUserSelect(user);
        }
    };

    const totalPages = Math.ceil(users.length / itemsPerPage);

    const handlePreviousPage = () => {
        setPage((prevPage) => Math.max(prevPage - 1, 0));
    };

    const handleNextPage = () => {
        setPage((prevPage) => Math.min(prevPage + 1, totalPages - 1));
    };

    // Get the users for the current page
    const startIndex = page * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const currentPageUsers = users.slice(startIndex, endIndex);

    return (
        <Card className="w-full h-5/7 max-w-5xl p-4 shadow-md">
            {/* Card Header */}
            <CardHeader>
                <CardTitle className="text-center">Registered Users</CardTitle>
            </CardHeader>
            {/* Card Content */}
            <CardContent>
                {/* Table */}
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
                                key={user.username}
                                onClick={() => {
                                    handleRowClick(user);
                                }}
                                className={
                                    selectedUser?.username === user.username
                                        ? "bg-gray-100"
                                        : ""
                                }
                                style={{ cursor: "pointer" }}
                            >
                                <TableCell>{user.username}</TableCell>
                                <TableCell>{user.name}</TableCell>
                                <TableCell>{user.email}</TableCell>
                                <TableCell>{user.role}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
                {/* Pagination Controls */}
                <div className="flex justify-between items-center mt-4">
                    <Button
                        onClick={handlePreviousPage}
                        disabled={page === 0}
                    >
                        Previous
                    </Button>
                    <span>
            Page {page + 1} of {totalPages}
          </span>
                    <Button
                        onClick={handleNextPage}
                        disabled={page >= totalPages - 1}
                    >
                        Next
                    </Button>
                </div>
            </CardContent>
        </Card>
    );
};
