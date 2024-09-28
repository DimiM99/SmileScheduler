import React, {useEffect, useState} from 'react';
import {useAuth} from "@/hooks/useAuth.ts";
import Layout from "@/components/layout.tsx";
import {UserManagementForm} from "@/components/userManagementForm.tsx";
import {User} from "@/models";
import {UserList} from "@/components/userList.tsx";
import {Role} from "@/models/enums/Role.ts";

const users: User[] = [
    {
        id: 1,
        username: "charlie",
        password: "password789",
        name: "Charlie Lee",
        email: "charlie.lee@example.com",
        role: Role.DOCTOR,
    },
    {
        id: 2,
        username: "diana",
        password: "password101",
        name: "Diana Prince",
        email: "diana.prince@example.com",
        role: Role.DOCTOR,
    },
    {
        id: 3,
        username: "alice",
        password: "password123", // Note: For testing purposes only
        name: "Alice Johnson",
        email: "alice.johnson@example.com",
        role: Role.ADMIN,
    },
    {
        id: 4,
        username: "bob",
        password: "password456",
        name: "Bob Smith",
        email: "bob.smith@example.com",
        role: Role.RECEPTIONIST,
    },
    {
        id: 5,
        username: "edward",
        password: "password202",
        name: "Edward King",
        email: "edward.king@example.com",
        role: Role.RECEPTIONIST,
    },
    {
        id: 6,
        username: "charlie",
        password: "password789",
        name: "Charlie Lee",
        email: "charlie.lee@example.com",
        role: Role.DOCTOR,
    },
    {
        id: 7,
        username: "diana",
        password: "password101",
        name: "Diana Prince",
        email: "diana.prince@example.com",
        role: Role.DOCTOR,
    },
    {
        id: 8,
        username: "bob",
        password: "password456",
        name: "Bob Smith",
        email: "bob.smith@example.com",
        role: Role.RECEPTIONIST,
    },
    {
        id: 9,
        username: "edward",
        password: "password202",
        name: "Edward King",
        email: "edward.king@example.com",
        role: Role.RECEPTIONIST,
    },
    {
        id: 10,
        username: "charlie",
        password: "password789",
        name: "Charlie Lee",
        email: "charlie.lee@example.com",
        role: Role.DOCTOR,
    },
    {
        id: 11,
        username: "diana",
        password: "password101",
        name: "Diana Prince",
        email: "diana.prince@example.com",
        role: Role.DOCTOR,
    }
    // Add more users as needed
];


const AdminDashboard: React.FC = () => {
    const {user} = useAuth ();

    const [loading, setLoading] = useState<boolean>(true);
    const [selectedUser, setSelectedUser] = useState<User | null>(null);

    const handleUserSelect = (user: User | null) => {
        setSelectedUser(user);
    };

    useEffect (() => {
        setLoading(true);
        setLoading(false);
    }, [user]);

    if (loading) {
        return <p>Loading...</p>;
    }

    if (!user) {
        return <p>User not authenticated.</p>;
    }

    return (
        <Layout
            user={user}
            left={
                <div className="flex flex-col justify-center items-center h-full">
                    <h2>Registered Users</h2>
                    <p>Here is a list of registered users</p>
                    <UserList
                        users={users}
                        selectedUser={selectedUser}
                        onUserSelect={handleUserSelect}
                    />
                </div>
            }
            right={
                <div className="flex flex-col justify-center items-center h-full">
                    <h2>Edit Users</h2>
                    <p>This is the content for the right section.</p>
                    <UserManagementForm current={null}/>
                </div>
            }
            leftWeight={5}
            rightWeight={2}
        />
    );
};

export default AdminDashboard;
