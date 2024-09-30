export enum AppointmentType {
    QUICKCHECK = 'QUICKCHECK',
    EXTENSIVE = 'EXTENSIVE',
    SURGERY = 'SURGERY'
}

export const appointmentDurations: Record<AppointmentType, number> = {
    [AppointmentType.QUICKCHECK]: 30,
    [AppointmentType.EXTENSIVE]: 60,
    [AppointmentType.SURGERY]: 120,
};