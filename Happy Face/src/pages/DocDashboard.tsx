import React, {useEffect, useState} from 'react';
import {fetchAppointments} from '../services/api';
import {Appointment} from '@/models';
import {useAuth} from "@/hooks/ausAuth.ts";
import Layout from "@/components/layout.tsx";

const DocDashboard: React.FC = () => {
    const {user} = useAuth ();
    const [appointments, setAppointments] = useState<Appointment[]> ([]);

    useEffect (() => {
        const loadAppointments = async (): Promise<void> => {
            if (user) {
                try {
                    const fetchedAppointments = await fetchAppointments (user.id);
                    setAppointments (fetchedAppointments);
                } catch (error) {
                    console.error ('Failed to fetch appointments:', error);
                }
            }
        };

        loadAppointments ().catch ((e: unknown) => {
            console.error (e);
        });
    }, [user]);

    return (
        <Layout
            top={<header>This is the Navbar</header>}
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
