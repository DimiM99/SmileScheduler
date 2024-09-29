import { UserResponse } from '@/models/services/responses/UserResponse';
import { User } from '@/models/User';
import {UserRequest} from "@/models/services/requests/UserRequest.ts"; // Adjust the path as necessary


export const mapUserToUserRequest = (user: User): UserRequest => {
    return {
        ...user,
        active: true
    };
}

export const mapUserToUserResponse = (user: User): UserResponse => {
    return {
        ...user,
        active: true
    };
}