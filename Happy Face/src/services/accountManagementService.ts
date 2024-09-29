import { AxiosResponse } from 'axios';
import { api } from './apiConfig';
import { IAccountManagementService } from "@/models/services/IAccountManagementService.ts";
import { UserRequest } from "@/models/services/requests/UserRequest.ts";
import { UserResponse } from "@/models/services/responses/UserResponse.ts";
import { handleApiError } from './errorHandler';
import {User} from "@/models";

export class AccountManagementService implements IAccountManagementService {
    private static _instance?: AccountManagementService;

    public static get Instance()
    {
        return this._instance ?? (this._instance = new this());
    }

    private constructor() {

    }

    async createUser(user: UserRequest): Promise<UserResponse> {
        try {
            const response: AxiosResponse<UserResponse> = await api.post('/account-management/user', user);
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    }

    async updateUser(user: UserResponse): Promise<UserResponse> {
        try {
            const response: AxiosResponse<UserResponse> = await api.put('/account-management/user', user);
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    }

    async deleteUser(user: UserResponse): Promise<void> {
        try {
            await api.delete('/account-management/user', { data: user });
        } catch (error) {
            throw handleApiError(error);
        }
    }


    async getUsers(): Promise<User[]> {
        try {
            const response: AxiosResponse<User[]> = await api.get('/account-management/users');
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    }
}
