import React, {useEffect, useMemo, useState} from 'react';
import {useAuth} from "@/hooks/useAuth.ts";
import Layout from "@/components/layout.tsx";
import WeekCalendar from "@/components/weekCalendar.tsx";
import {AppointmentForm, FormValues} from "@/components/appointmentForm.tsx";
import {AppointmentService} from "@/services/appointmentService.ts";
import {AppointmentRequest, Doctor} from "@/models";
import {AppointmentResponse} from "@/models/services/responses/AppointmentResponse.ts";
import {format} from "date-fns";
import {AppointmentUpdateRequest} from "@/models/services/requests/AppointmentUpdateRequest.ts";

const RecDashboard: React.FC = () => {
    const {user} = useAuth ();
    const [loading, setLoading] = useState<boolean> (true);
    const [doctors, setDoctors] = useState<Doctor[]> ([]);
    const [selectedDoctor, setSelectedDoctor] = useState<Doctor> ({} as Doctor);
    const [events, setEvents] = useState<AppointmentResponse[]> ([]);
    const [selectedAppointment, setSelectedAppointment] = useState<AppointmentResponse | null> (null);
    const [currentDate, setCurrentDate] = useState (new Date ());

    const appointmentService = useMemo(() => new AppointmentService(), []);

    const handleDateChange = async (date : Date)=> {
        setCurrentDate(date);
        const res = await appointmentService.getAppointmentsForDoctor (selectedDoctor.id, format(date, 'yyyy-MM-dd'));
        setEvents (res);
    }

    const handleDoctorChange = async (doctor: Doctor) => {
        setSelectedDoctor(doctor);
        const res = await appointmentService.getAppointmentsForDoctor (doctor.id, format(currentDate, 'yyyy-MM-dd'));
        setEvents(res)
    }

    const handleEventSelect = (event: AppointmentResponse) => {
        setSelectedAppointment (event);
    };

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

    function fetchAppointments(id: number, currentDate: Date) {
        appointmentService.getAppointmentsForDoctor(id, currentDate.toISOString().split('T')[0])
            .then((res) => {
                setEvents(res);
            })
            .catch((error: unknown) => {
                console.error('Error fetching appointments:', error);
            });
    }

    const handleAppointmentSubmit = (data: FormValues, creation: boolean) => {
        console.log("data", data);
        if (creation) {
            appointmentService.createAppointment(formValuesToAppointmentRequest(data))
                .then(() => {
                    fetchAppointments(selectedDoctor.id, currentDate);
                })
                .catch((error: unknown) => {
                    console.error('Error creating appointment:', error);
                });
        } else {
            appointmentService.updateAppointment(formValuesToAppointmentUpdateRequest(data))
                .then(() => {
                    fetchAppointments(selectedDoctor.id, currentDate);
                })
                .catch((error: unknown) => {
                    console.error('Error updating appointment:', error);
                });
        }
        setSelectedAppointment(null);
    };

    useEffect (() => {
        const initData = async (docs: Doctor[]) => {
            setDoctors (docs);
            if (docs.length > 0) {
                setSelectedDoctor (docs[0]);
                setEvents (
                    await appointmentService.getAppointmentsForDoctor(docs[0].id, currentDate.toISOString().split('T')[0])
                );
            }
        }
        const init = async () => {
            setLoading (true);
            try {
                await appointmentService.fetchDoctors().then((res) => { void initData(res); });
            } catch (error) {
                console.error ('Error fetching initial data:', error);
            } finally {
                setLoading (false);
            }
        };
        if (user) {
            void init();
        }
    }, [user, appointmentService]);


    if (loading) {
        return <p>Loading...</p>;
    }

    if (!user) {
        return <p>User not authenticated.</p>;
    }

    function dropSelectedAppointment() {
        setSelectedAppointment(null);
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
                    newDoctorSelected={handleDoctorChange}
                    dropSelectedAppointment={dropSelectedAppointment}
                />
            }
            leftWeight={3}
            rightWeight={2}
        />
    );
};

export default RecDashboard;

