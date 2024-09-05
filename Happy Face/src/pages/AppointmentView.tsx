import React, {useEffect, useState} from 'react';
import {fetchAppointments} from '../services/api';
import {Appointment} from '@/models';
import {useTokenAuth} from "@/hooks/useTokenAuth.ts";

const AppointmentView: React.FC = () => {
    const {token} = useTokenAuth ();
    const [appointments, setAppointments] = useState<Appointment[]> ([]);

    useEffect (() => {
        const loadAppointments = async (): Promise<void> => {
            if (!token) {
                return;
            }

            try {
                const fetchedAppointments = await fetchAppointments (token);
                setAppointments (fetchedAppointments);
            } catch (error) {
                console.error ('Failed to fetch appointments:', error);
            }
        };

        loadAppointments ().catch ((e: unknown) => {
            console.error (e);
        });
    }, [token]);
    return (
        <div>
            <h1>Patient Dashboard</h1>
            {/* Render appointments and other patient-specific content */}
        </div>
    );
}

export default AppointmentView;
