import React, {useEffect, useState} from 'react';
import {useAuth} from "@/hooks/useAuth.ts";
import Layout from "@/components/layout.tsx";
import {UserManagementForm} from "@/components/userManagementForm.tsx";
import {User} from "@/models";
import {UserList} from "@/components/userList.tsx";
import {AccountManagementService} from "@/services/accountManagementService.ts";


const AdminDashboard: React.FC = () => {
    const {user} = useAuth ();
    const [users, setUsers] = useState<User[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [selectedUser, setSelectedUser] = useState<User | null>(null);
    const [error, setError] = useState<string | null>(null);

    const handleUserSelect = (user: User | null) => {
        setSelectedUser(user);
    };

    const fetchUsers = async () => {
        setLoading(true); // Start loading
        setError(null); // Reset previous errors
        try {
            const fetchedUsersResponse: User[] = await AccountManagementService.Instance.getUsers();
            setUsers(fetchedUsersResponse);
        } catch (err) {
            // Handle errors (assuming handleApiError returns a string message)
            if (typeof err === 'string') {
                setError(err);
            } else if (err instanceof Error) {
                setError(err.message);
            } else {
                setError('An unexpected error occurred.');
            }
        } finally {
            setLoading(false); // End loading
        }
    };

    useEffect (() => {

        void fetchUsers();
    }, [user]);

    const handleUserUpdated = () => {
        void fetchUsers();
        setSelectedUser(null); // Clear the selected user after an operation
    };

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
                    <UserList
                        users={users}
                        selectedUser={selectedUser}
                        onUserSelect={handleUserSelect}
                    />
                    {error && <p className="text-red-600">{error}</p>}
                </div>
            }
            right={
                <div className="flex flex-col justify-center items-center h-full">
                    <UserManagementForm
                        current={selectedUser}
                        onUserUpdated={handleUserUpdated}
                    />
                </div>
            }
            leftWeight={4}
            rightWeight={2}
        />
    );
};

export default AdminDashboard;
