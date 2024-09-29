import React, {useEffect, useState} from 'react';
import {useAuth} from "@/hooks/useAuth.ts";
import Layout from "@/components/layout.tsx";
import {AppointmentList} from "@/components/appointmentList.tsx";
import {AppointmentService} from "@/services/appointmentService.ts";
import {AppointmentResponse} from "@/models/services/responses/AppointmentResponse.ts";
import {AppointmentDetailCard} from "@/components/appointmentDetailsCard.tsx";

interface DashboardState {
    appointments: AppointmentResponse[];
    selectedAppointment: AppointmentResponse | null;
    loading: boolean;
    error: string | null;
}

const DocDashboard: React.FC = () => {
    const {user} = useAuth ();

    const [dashboardState, setDashboardState] = useState<DashboardState>({
        appointments: [],
        selectedAppointment: null,
        loading: true,
        error: null,
    });

    const fetchAppointments = async () => {
        if (!user) {
            setDashboardState(prev => ({
                ...prev,
                loading: false,
                appointments: [],
                selectedAppointment: null,
            }));
            return;
        }

        setDashboardState(prev => ({ ...prev, loading: true, error: null }));

        try {
            const currentDate = new Date().toISOString().split('T')[0]; // YYYY-MM-DD format
            const appointments = await AppointmentService.Instance.getAppointmentsForDoctor(
                24,
                "2024-10-07",
                true
            );

            setDashboardState(prev => ({
                ...prev,
                appointments,
                loading: false,
            }));
        } catch (error: any) {
            setDashboardState(prev => ({
                ...prev,
                loading: false,
                error: error.message || 'Failed to fetch appointments.',
            }));
        }
    };

    const handleAppointmentSelected = (appointment: AppointmentResponse | null) => {
        setDashboardState(prev => ({
            ...prev,
            selectedAppointment: appointment,
        }));
    };

    const unsetSelectedAppointment = () => {
        setDashboardState(prev => ({
            ...prev,
            selectedAppointment: null,
        }));
    }

    useEffect (() => {
        void fetchAppointments()
    }, [user]);

    if (dashboardState.loading) {
        return <p>Loading...</p>;
    }

    if (!user) {
        return <p>User not authenticated.</p>; // Optionally handle unauthenticated state
    }

    return (
        <Layout
            user={user}
            left={
                <div className="flex flex-col justify-center items-center h-full">
                    <AppointmentList
                        appointments={dashboardState.appointments}
                        onAppointmentSelect={handleAppointmentSelected}
                        selectedAppointment={dashboardState.selectedAppointment}
                    />
                </div>
            }
            right={
                <div className="flex flex-col justify-center items-center h-full">

                    {dashboardState.selectedAppointment && (

                        <AppointmentDetailCard
                            onClose={unsetSelectedAppointment}
                        />

                    )}
                </div>
            }
            leftWeight={2.5}
            rightWeight={3}
        />
    );
};

export default DocDashboard;
