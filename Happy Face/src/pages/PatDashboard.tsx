import React, {useEffect, useState} from 'react';
import {Appointment} from '@/models';
import {useTokenAuth} from "@/hooks/useTokenAuth.ts";

const PatDashboard: React.FC = () => {
    const {token} = useTokenAuth ();

    // @ts-expect-error because appointments are not yet implemented
    const [appointments, setAppointments] = useState<Appointment[]> ([]);

    useEffect (() => {

    }, [token]);
    return (
        <div>
            <h1>Appointment View</h1>
            {/* Render appointments and other patient-specific content */}
        </div>
    );
}

export default PatDashboard;
