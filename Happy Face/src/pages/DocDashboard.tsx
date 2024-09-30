import React, {useEffect, useState} from 'react';
import {useAuth} from "@/hooks/useAuth";
import Layout from "@/components/layout";
import {AppointmentList} from "@/components/appointmentList";
import {AppointmentService} from "@/services/appointmentService";
import {AppointmentResponse} from "@/models/services/responses/AppointmentResponse";
import {AppointmentDetailCard} from "@/components/appointmentDetailsCard";
import {Toaster} from "@/components/ui/sonner.tsx";

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
                    user.id,
                    currentDate,
                    true
                );

                setDashboardState(prev => ({
                    ...prev,
                    appointments,
                    loading: false,
                }));
            } catch (error: unknown) {
                let errorMessage = 'Failed to fetch appointments.';
                if (error instanceof Error) {
                    errorMessage = error.message;
                }
                setDashboardState(prev => ({
                    ...prev,
                    loading: false,
                    error: errorMessage,
                }));
            }
        };

        void fetchAppointments()
    }, [user]);

    if (dashboardState.loading) {
        return <p>Loading...</p>;
    }

    if (!user) {
        return <p>User not authenticated.</p>; // Optionally handle unauthenticated state
    }

    return (
        <>
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
                                appointment={dashboardState.selectedAppointment}
                                onClose={unsetSelectedAppointment}
                            />

                        )}
                    </div>
                }
                leftWeight={2.5}
                rightWeight={3}
            />
            <Toaster className=""/>
        </>

    );
};

export default DocDashboard;
