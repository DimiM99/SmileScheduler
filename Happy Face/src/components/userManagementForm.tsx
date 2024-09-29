import React, {useEffect} from 'react';
import {Button} from "@/components/ui/button";
import {Input} from "@/components/ui/input";
import {Label} from "@/components/ui/label";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select";
import {Role} from "@/models/enums/Role";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {useState} from "react";
import {User} from "@/models";
import {AccountManagementService} from "@/services/accountManagementService.ts";
import {mapUserToUserRequest, mapUserToUserResponse} from "@/helpers/userMapper.ts";

type formData = {
    username: string,
    password: string,
    aName: string,
    email: string,
    role: Role
}

type ErrorMessages = {
    [key in keyof formData]: string
}

interface FormProps {
    current: User | null,
    onUserUpdated: () => void
}


export const UserManagementForm: React.FC<FormProps> = ({ current, onUserUpdated }) => {

    const [formState, setFormState] = useState<formData>({
        username: "",
        password: "",
        aName: "",
        email: "",
        role: Role.RECEPTIONIST,
    });

    useEffect(() => {
        if (current) {
            setFormState({
                username: current.username,
                password: current.password,
                aName: current.name,
                email: current.email,
                role: current.role,
            });
        } else {
            // Reset form when no user is selected
            setFormState({
                username: "",
                password: "",
                aName: "",
                email: "",
                role: Role.RECEPTIONIST,
            });
        }
    }, [current]);


    const [errors, setErrors] = useState<ErrorMessages>({} as ErrorMessages)
    const [loading, setLoading] = useState(false);
    const [operationStatus, setOperationStatus] = useState<string | null>(null);

    const forbidden = ['´', '\\', '*', '$', '#', '^', '%', '&']

    const containsForbidden = (str: string): boolean => {
        return forbidden.some((char) => str.includes(char))
    }


    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target
        setFormState(prev => ({...prev, [name]: value}))
    }

    const handleRoleChange = (value: string) => {
        setFormState(prev => ({...prev, role: value as Role}))
    }

    const isFormValid = (): boolean => {
        const newErrors: ErrorMessages = {} as ErrorMessages
        const isEmailValid = /\S+@\S+\.\S+/; // ^[^\s@]+@[^\s@]+\.[^\s@]+$

        if (formState.username.length < 4) {
            newErrors.username = "User name has to be at least 3 characters"
        }

        if (formState.password.length < 6) {
            newErrors.password = "Password has to be at least 6 characters"
        }

        if (formState.aName.length < 3) {
            newErrors.aName = "Name has to be at least 3 characters"
        }

        if (!isEmailValid.test(formState.email)) {
            newErrors.email = "Email is invalid"
        }

        setErrors(newErrors)
        return Object.keys(newErrors).length === 0;

    }

    const createUser = async () => {
        setLoading(true);
        try {
            await AccountManagementService.Instance.createUser(mapUserToUserRequest({
                username: formState.username,
                password: formState.password,
                name: formState.aName,
                email: formState.email,
                role: formState.role,
            } as User
            ));
            setOperationStatus("User created successfully");
            onUserUpdated();
        } catch (error) {
            setOperationStatus("Error creating user");
            console.error("Error creating user:", error);
        } finally {
            setLoading(false);
        }
    };

    const updateUser = async () => {
        if (!current) return;
        setLoading(true);
        try {
            await AccountManagementService.Instance.updateUser(mapUserToUserResponse({
                username: formState.username,
                name: formState.aName,
                password: formState.password,
                email: formState.email,
                role: formState.role,
            } as User
            ));
            setOperationStatus("User updated successfully");
            onUserUpdated();
        } catch (error) {
            setOperationStatus("Error updating user");
            console.error("Error updating user:", error);
        } finally {
            setLoading(false);
        }
    };

    const deleteUser = async () => {
        if (!current) return;
        setLoading(true);
        try {
            await AccountManagementService.Instance.deleteUser(mapUserToUserResponse(current));
            setOperationStatus("User deleted successfully");
            onUserUpdated();
        } catch (error) {
            setOperationStatus("Error deleting user");
            console.error("Error deleting user:", error);
        } finally {
            setLoading(false);
        }
    };



    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (isFormValid()) {
            if (current) {
                await updateUser();
                return
            }
            await createUser();

        }
    };


    return (
        <Card className="w-full h-4/5 max-w-md p-4 shadow-md"> {/* ShadCN card */}
            <CardHeader>
                <CardTitle className="text-center">
                    {
                        current ? "Edit User" : "Create User"
                    }
                </CardTitle> {/* Card title */}
            </CardHeader>
            <CardContent className="h-5/6">
                <form onSubmit={ handleSubmit } className="h-full flex flex-col justify-between">
                    <div className="flex flex-col space-y-1.5">
                        <Label htmlFor="username">Username</Label>
                        <Input
                            id="username"
                            name="username"
                            placeholder="Enter your username"
                            value={formState.username}
                            onChange={handleChange}
                        />
                        {
                            errors.username &&
                            <div className="flex flex-col space-y-1.5 items-center text-red-600 text-sm">
                                <Label>{errors.username}</Label>
                            </div>
                        }
                    </div>
                    <div className="flex flex-col space-y-1.5">
                        <Label htmlFor="password">Password</Label>
                        <Input
                            id="password"
                            name="password"
                            type="password"
                            placeholder="Enter your password"
                            value={formState.password}
                            onChange={handleChange}
                        />
                    </div>
                    {
                        errors.password &&
                        <div className="flex flex-col space-y-1.5 items-center text-red-600 text-sm">
                            <Label>{errors.password}</Label>
                        </div>
                    }
                    <div className="flex flex-col space-y-1.5">
                        <Label htmlFor="aName">Name</Label>
                        <Input
                            id="aName"
                            name="aName"
                            placeholder="Enter your full name"
                            value={formState.aName}
                            onChange={handleChange}
                        />
                    </div>
                    {
                        errors.aName &&
                        <div className="flex flex-col space-y-1.5 items-center text-red-600 text-sm">
                            <Label>{errors.aName}</Label>
                        </div>
                    }
                    <div className="flex flex-col space-y-1.5">
                        <Label htmlFor="email">email</Label>
                        <Input
                            id="email"
                            name="email"
                            type="mail"
                            placeholder="Enter your email"
                            value={formState.email}
                            onChange={handleChange}
                        />
                    </div>
                    {
                        errors.email &&
                        <div className="flex flex-col space-y-1.5 items-center text-red-600 text-sm">
                            <Label>{errors.email}</Label>

                        </div>
                    }
                    <div className="flex flex-col space-y-1.5">
                        <Label htmlFor="role">Role</Label>
                        <Select
                            value={formState.role}
                            onValueChange={handleRoleChange}
                        >
                            <SelectTrigger id="role">
                                <SelectValue placeholder="Select a role"/>
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value={Role.RECEPTIONIST}>Receptionist</SelectItem>
                                <SelectItem value={Role.DOCTOR}>Doctor</SelectItem>

                            </SelectContent>
                        </Select>
                    </div>

                    <Button className="mt-7 w-full" type="submit">
                        {
                            current ? "Apply Changes" : "Create User"
                        }
                    </Button>
                    {
                        current && (
                            <Button
                                variant="destructive"
                                className="w-full "
                                onClick={deleteUser}
                            >
                                Delete User
                            </Button>
                        )
                    }

                    {operationStatus && (
                        <div className={`mt-2 text-center ${operationStatus.includes("Error") ? "text-red-500" : "text-green-500"}`}>
                            {operationStatus}
                        </div>
                    )}
                </form>
            </CardContent>
        </Card>
    );
};
