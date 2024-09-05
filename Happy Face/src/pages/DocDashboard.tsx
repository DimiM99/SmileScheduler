import React, {useEffect, useState} from 'react';
import {fetchAppointments} from '../services/api';
import {Appointment} from '@/models';
import {useAuth} from "@/hooks/ausAuth.ts";

const DocDashbaorad: React.FC = () => {
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
        <div>
            <h1>Patient Dashboard</h1>
            {/* Render appointments and other patient-specific content */}
        </div>
    );
};

export default DocDashbaorad;
