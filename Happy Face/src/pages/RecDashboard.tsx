import React, {useCallback, useEffect, useState} from 'react';
import {useAuth} from "@/hooks/useAuth.ts";
import Layout from "@/components/layout.tsx";
import {AppointmentForm, FormValues} from "@/components/appointmentForm.tsx";
import {AppointmentRequest, Doctor} from "@/models";
import {format} from "date-fns";
import {AppointmentUpdateRequest} from "@/models/services/requests/AppointmentUpdateRequest.ts";
import WeekCalendar from "@/components/weekCalendar.tsx";
import {useAppointmentStore} from "@/hooks/zustand/useAppointmentStore.ts";
import {AppointmentResponse} from "@/models/services/responses/AppointmentResponse.ts";

const RecDashboard: React.FC = () => {
    const {user} = useAuth();
    const [selectedAppointment, setSelectedAppointment] = useState<AppointmentResponse | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const {
        doctors,
        selectedDoctor,
        events,
        currentDate,
        fetchDoctors,
        setSelectedDoctor,
        setCurrentDate,
        createAppointment,
        updateAppointment,
        fetchAppointments,
    } = useAppointmentStore();

    useEffect(() => {
        const initializeData = async () => {
            if (user) {
                setIsLoading(true);
                await fetchDoctors();
                setIsLoading(false);
            }
        };
        // eslint-disable-next-line @typescript-eslint/no-floating-promises
        initializeData();
    }, [user, fetchDoctors]);

    const handleAppointmentSubmit = useCallback((data: FormValues, creation: boolean) => {
        if (creation) {
            //eslint-disable-next-line @typescript-eslint/no-floating-promises
            createAppointment(formValuesToAppointmentRequest(data));
        } else {
            // eslint-disable-next-line @typescript-eslint/no-floating-promises
            updateAppointment(formValuesToAppointmentUpdateRequest(data));
        }
        setSelectedAppointment(null);
    }, [createAppointment, updateAppointment]);

    function formValuesToAppointmentRequest(formValues: FormValues): AppointmentRequest {
        return {
            title: `Appointment for ${formValues.patientName}`,
            doctorId: formValues.doctorId,
            start: formValues.startTime,
            appointmentType: formValues.appointmentType,
            patient: {
                id: formValues.patientId,
                name: formValues.patientName,
                birthdate: format(formValues.patientBirthdate, 'yyyy-MM-dd'),
                email: formValues.patientEmail,
                insuranceNumber: formValues.patientInsuranceNumber,
                insuranceProvider: formValues.patientInsuranceProvider,
                phoneNumber: formValues.patientPhoneNumber,
            },
        };
    }

    function formValuesToAppointmentUpdateRequest(data: FormValues): AppointmentUpdateRequest {
        return {
            // eslint-disable-next-line @typescript-eslint/ban-ts-comment
            // @ts-expect-error
            id: selectedAppointment.id,
            title: `Appointment for ${data.patientName}`,
            // eslint-disable-next-line @typescript-eslint/ban-ts-comment
            // @ts-expect-error
            patientId: data.patientId,
            doctorId: data.doctorId,
            start: data.startTime,
            appointmentType: data.appointmentType,
        };
    }

    const handleDoctorChange = useCallback(async (doctor: Doctor) => {
        console.log(doctor);
        setSelectedDoctor(doctor);
        await fetchAppointments();
    }, [setSelectedDoctor, fetchAppointments]);

    const handleDateChange = useCallback(async (date: Date) => {
        setCurrentDate(date);
        await fetchAppointments();
    }, [setCurrentDate, fetchAppointments]);

    const handleEventSelect = useCallback((event: AppointmentResponse) => {
        setSelectedAppointment(event);
    }, []);

    if (!user) {
        return <p>User not authenticated.</p>;
    }

    if (isLoading) {
        return <p>Loading...</p>;
    }

    return (
        <Layout
            user={user}
            left={
                <WeekCalendar
                    events={events}
                    isBookingMode={false}
                    onDoctorChange={handleDoctorChange}
                    onDateChange={handleDateChange}
                    onEventSelect={handleEventSelect}
                    doctors={doctors}
                    selectedDoctor={selectedDoctor}
                    currentlySelectedDate={currentDate}
                />
            }
            right={
                <AppointmentForm
                    selectedAppointment={selectedAppointment}
                    doctors={doctors}
                    onSubmit={handleAppointmentSubmit}
                    currentlySelectedDoctor={selectedDoctor}
                    // eslint-disable-next-line @typescript-eslint/no-misused-promises
                    newDoctorSelected={handleDoctorChange}
                    dropSelectedAppointment={() => { setSelectedAppointment(null); }}
                />
            }
            leftWeight={3}
            rightWeight={2}
        />
    );
};

export default RecDashboard;

