import {create} from 'zustand';
import {format} from 'date-fns';
import {AppointmentRequest, Doctor} from "@/models";
import {AppointmentResponse} from "@/models/services/responses/AppointmentResponse.ts";
import {AppointmentUpdateRequest} from "@/models/services/requests/AppointmentUpdateRequest.ts";
import {AppointmentType} from "@/models/enums/AppointmentType.ts";
import {AppointmentService} from "@/services/appointmentService.ts";

interface AppointmentState {
    doctors: Doctor[];
    selectedDoctor: Doctor | null;
    events: AppointmentResponse[];
    currentDate: Date;
    availableSlots: string[];

    fetchDoctors: () => Promise<void>;
    setSelectedDoctor: (doctor: Doctor) => void;
    fetchAppointments: () => Promise<void>;
    setCurrentDate: (date: Date) => void;
    createAppointment: (appointment: AppointmentRequest) => Promise<void>;
    updateAppointment: (appointment: AppointmentUpdateRequest) => Promise<void>;
    fetchAvailableSlots: (doctorId: number, date: Date, appointmentType: AppointmentType) => Promise<void>;
}

const appointmentService = new AppointmentService();

export const useAppointmentStore = create<AppointmentState>((set, get) => ({
    doctors: [],
    selectedDoctor: null,
    events: [],
    currentDate: new Date(),
    availableSlots: [],

    fetchDoctors: async () => {
        const doctors = await appointmentService.fetchDoctors();
        set({doctors});
        if (doctors.length > 0 && !get().selectedDoctor) {
            set({selectedDoctor: doctors[0]});
        }
    },

    setSelectedDoctor: (doctor) => {
        set({selectedDoctor: doctor});
    },

    fetchAppointments: async () => {
        const {selectedDoctor, currentDate} = get();
        if (selectedDoctor) {
            const events = await appointmentService.getAppointmentsForDoctor(
                selectedDoctor.id,
                format(currentDate, 'yyyy-MM-dd')
            );
            set({events});
        }
    },

    setCurrentDate: (date) => {
        set({currentDate: date});
    },

    createAppointment: async (appointment) => {
        await appointmentService.createAppointment(appointment);
        await get().fetchAppointments();
    },

    updateAppointment: async (appointment) => {
        await appointmentService.updateAppointment(appointment);
        await get().fetchAppointments();
    },

    fetchAvailableSlots: async (doctorId, date, appointmentType) => {
        const slots = await appointmentService.getFreeSlots(
            doctorId,
            format(date, 'yyyy-MM-dd'),
            appointmentType
        );
        set({availableSlots: slots});
    },
}));

