import React, {useEffect, useState} from 'react';
import {fetchAppointments} from '../services/api';
import {Appointment} from '@/models';
import {useAuth} from "@/hooks/ausAuth.ts";
import Layout from "@/components/layout.tsx";

const RecDashboard: React.FC = () => {
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
