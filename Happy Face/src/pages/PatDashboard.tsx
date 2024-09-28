import React, {useEffect} from 'react';
import {useTokenAuth} from "@/hooks/useTokenAuth.ts";

const PatDashboard: React.FC = () => {
    const {token} = useTokenAuth ();


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
