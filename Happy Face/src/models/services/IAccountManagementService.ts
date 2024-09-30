import {UserResponse} from "@/models/services/responses/UserResponse.ts";
import {UserRequest} from "@/models/services/requests/UserRequest.ts";
import {User} from "@/models";


export interface IAccountManagementService {
    createUser: (user: UserRequest) => Promise<UserResponse>;
    updateUser: (user: UserResponse) => Promise<UserResponse>;
    deleteUser: (user: UserResponse) => Promise<void>;
    getUsers: () => Promise<User[]>;
}