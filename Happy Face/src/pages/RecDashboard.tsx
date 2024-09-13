import React, {useEffect, useState} from 'react';
import {fetchAppointments} from '../services/api';
import {Appointment} from '@/models';
import {useAuth} from "@/hooks/ausAuth.ts";
import Layout from "@/components/layout.tsx";

const RecDashboard: React.FC = () => {
    const {user} = useAuth ();
    const [appointments, setAppointments] = useState<Appointment[]> ([]);
    const [loading, setLoading] = useState<boolean>(true);

    useEffect (() => {
        const loadAppointments = async (): Promise<void> => {
            if (user) {
                try {
                    const fetchedAppointments = await fetchAppointments(user.username);
                    setAppointments(fetchedAppointments);
                } catch (error) {
                    console.error('Failed to fetch appointments:', error);
                } finally {
                    setLoading(false);  // Set loading to false when done
                }
            } else {
                setLoading(false);  // No user, stop loading
            }
        };

        loadAppointments ().catch ((e: unknown) => {
            console.error (e);
            setLoading(false);
        });
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
                <div>
                    <h2>Week Preview</h2>
                    <p>This is the content for the left section.</p>
                </div>
            }
            right={
                <div>
                    <h2>Appointment Configuration</h2>
                    <p>This is the content for the right section.</p>
                </div>
            }
            leftWeight={3}
            rightWeight={2}
        />
    );
};

export default RecDashboard;
