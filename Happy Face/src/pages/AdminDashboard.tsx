import React, {useEffect, useState} from 'react';
import {useAuth} from "@/hooks/useAuth.ts";
import Layout from "@/components/layout.tsx";
import {UserManagementForm} from "@/components/userManagementForm.tsx";
import {User} from "@/models";
import {UserList} from "@/components/userList.tsx";
import {AccountManagementService} from "@/services/accountManagementService.ts";
import {Toaster} from "@/components/ui/sonner.tsx";


const AdminDashboard: React.FC = () => {
    const {user} = useAuth ();

    const [dashboardState, setDashboardState] = useState({
        users: [] as User[],
        selectedUser: null as User | null,
        loading: true,
        error: null as string | null,
    });



    const handleUserSelect = (user: User | null) => {
        setDashboardState(prevState => ({
            ...prevState,
            selectedUser: user,
        }));
    };

    const fetchUsers = async () => {
        setDashboardState(prevState => ({
            ...prevState,
            loading: true,
        }));
        setDashboardState(prevState => ({
            ...prevState,
            error: null,
        }));
        try {
            const fetchedUsersResponse: User[] = await AccountManagementService.Instance.getUsers();
            setDashboardState(prevState => ({
                ...prevState,
                users: fetchedUsersResponse,
            }));
        } catch (err) {
            // Handle errors (assuming handleApiError returns a string message)
            if (typeof err === 'string') {
                setDashboardState(prevState => ({
                    ...prevState,
                    error: err,
                }));
            } else if (err instanceof Error) {
                setDashboardState(prevState => ({
                    ...prevState,
                    error: err.message,
                }));
            } else {
                setDashboardState(prevState => ({
                    ...prevState,
                    error: 'An unexpected error occurred.',
                }));
            }
        } finally {
            setDashboardState(prevState => ({
                ...prevState,
                loading: false,
            }));
        }
    };

    useEffect (() => {

        void fetchUsers();
    }, [user]);

    const handleUserUpdated = () => {
        void fetchUsers();
        setDashboardState(prevState => ({
            ...prevState,
            selectedUser: null,
        }));
    };

    if (dashboardState.loading) {
        return <p>Loading...</p>;
    }

    if (!user) {
        return <p>User not authenticated.</p>;
    }

    return (
        <div>
            <Layout
                user={user}
                left={
                    <div className="flex flex-col justify-center items-center h-full">
                        <UserList
                            users={dashboardState.users}
                            selectedUser={dashboardState.selectedUser}
                            onUserSelect={handleUserSelect}
                        />
                        {dashboardState.error && <p className="text-red-600">{dashboardState.error}</p>}
                    </div>
                }
                right={
                    <div className="flex flex-col justify-center items-center h-full">
                        <UserManagementForm
                            current={dashboardState.selectedUser}
                            onUserUpdated={handleUserUpdated}
                        />
                    </div>
                }
                leftWeight={4}
                rightWeight={2}>
            </Layout>
            <Toaster className=""/>
        </div>
    );
};

export default AdminDashboard;
