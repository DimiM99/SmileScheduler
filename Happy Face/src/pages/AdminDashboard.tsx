import React, {useEffect, useState} from 'react';
import {useAuth} from "@/hooks/useAuth.ts";
import Layout from "@/components/layout.tsx";
import {UserManagementForm} from "@/components/userManagementForm.tsx";

const AdminDashboard: React.FC = () => {
    const {user} = useAuth ();

    const [loading, setLoading] = useState<boolean>(true);

    useEffect (() => {
        setLoading(true);
        setLoading(false);
    }, [user]);

    if (loading) {
        return <p>Loading...</p>;
    }

    if (!user) {
        return <p>User not authenticated.</p>; // Optionally handle unauthenticated state
    }

    return (
        <Layout
            user={user}
            left={
                <div>
                    <h2>Registered Users</h2>
                    <p>Here is a list of registered users</p>
                </div>
            }
            right={
                <div>
                    <h2>Edit Users</h2>
                    <p>This is the content for the right section.</p>
                    <UserManagementForm current={null}/>
                </div>
            }
            leftWeight={2}
            rightWeight={3}
        />
    );
};

export default AdminDashboard;
