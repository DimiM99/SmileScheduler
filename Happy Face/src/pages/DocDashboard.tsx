import React, {useEffect, useState} from 'react';
import {Appointment} from '@/models';
import {useAuth} from "@/hooks/useAuth.ts";
import Layout from "@/components/layout.tsx";

const DocDashboard: React.FC = () => {
    const {user} = useAuth ();

    // @ts-expect-error because appointments are not yet implemented
    const [appointments, setAppointments] = useState<Appointment[]> ([]);
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
                    <h2>Appointments List</h2>
                    <p>This is the content for the left section.</p>
                </div>
            }
            right={
                <div>
                    <h2>Appointment Details</h2>
                    <p>This is the content for the right section.</p>
                </div>
            }
            leftWeight={2}
            rightWeight={3}
        />
    );
};

export default DocDashboard;
