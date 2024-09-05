import {User} from './User';

export type {Patient} from './Patient';
export type {AppointmentType} from './AppointmentType';
export type {Appointment} from './Appointment';
export type {Doctor} from './Doctor';
export type {User} from './User';

export interface LoginResponse {
    token: string;
    user: User;
}

export interface ApiError {
    status: number;
    message: string;
}
