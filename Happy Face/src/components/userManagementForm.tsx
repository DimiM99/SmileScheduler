import React, {useEffect, useState} from 'react';
import {Button} from "@/components/ui/button";
import {Input} from "@/components/ui/input";
import {Label} from "@/components/ui/label";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select";
import {Role} from "@/models/enums/Role";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
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

interface StatusState {
    errors: ErrorMessages,
    loading: boolean,
    operationStatus: string | null
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
    
    const [statusState, setStatusState] = useState<StatusState>({
        errors: {} as ErrorMessages,
        loading: false,
        operationStatus: null
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
        }else if (containsForbidden(formState.username)) {
            newErrors.username = "Username contains forbidden characters";
        }

        if (formState.password.length < 6) {
            newErrors.password = "Password has to be at least 6 characters"
        } else if (containsForbidden(formState.password)) {
            newErrors.password = "Password contains forbidden characters";
        }


        if (formState.aName.length < 3) {
            newErrors.aName = "Name has to be at least 3 characters"
        }else if (containsForbidden(formState.aName)) {
            newErrors.aName = "Name contains forbidden characters";
        }

        if (!isEmailValid.test(formState.email)) {
            newErrors.email = "Email is invalid"
        }else if (containsForbidden(formState.email)) {
            newErrors.email = "Email contains forbidden characters";
        }

        setStatusState(prev => ({...prev, errors: newErrors}))
        return Object.keys(newErrors).length === 0;

    }

    const createUser = async () => {
        setStatusState(prev => ({...prev, loading: true}))
        try {
            await AccountManagementService.Instance.createUser(mapUserToUserRequest({
                username: formState.username,
                password: formState.password,
                name: formState.aName,
                email: formState.email,
                role: formState.role,
            } as User
            ));
            setStatusState(prev => ({...prev, loading: false}))
            onUserUpdated();
        } catch (error) {
            setStatusState(prev => ({...prev, operationStatus: "Error creating user"}))
            console.error("Error creating user:", error);
        } finally {
            setStatusState(prev => ({...prev, loading: false}))
        }
    };

    const updateUser = async () => {
        if (!current) return;
        setStatusState(prev => ({...prev, loading: true}))

        const toUpdate: User = {
            id: current.id,
            username: current.username,
            password: formState.password,
            name: current.name,
            email: formState.email,
            role: current.role,
        }

        try {
            await AccountManagementService.Instance.updateUser(mapUserToUserResponse(toUpdate));
            setStatusState(prev => ({...prev, operationStatus: "User updated successfully"}))
            onUserUpdated();
        } catch (error) {
            setStatusState(prev => ({...prev, operationStatus: "Error updating user"}))
            console.error("Error updating user:", error);
        } finally {
            setStatusState(prev => ({...prev, loading: false}))  
        }
    };

    const deleteUser = async () => {
        if (!current) return;
        setStatusState(prev => ({...prev, loading: true}))
        try {
            await AccountManagementService.Instance.deleteUser(mapUserToUserResponse(current));
            setStatusState(prev => ({...prev, operationStatus: "User deleted successfully"}))
            onUserUpdated();
        } catch (error) {
            setStatusState(prev => ({...prev, operationStatus: "Error deleting user"}))
        } finally {
            setStatusState(prev => ({...prev, loading: false}))
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

    if (statusState.loading) {
        return <p>Loading...</p>;
    }


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
                <form onSubmit={(e) => {
                    void handleSubmit(e)
                } } className="h-full flex flex-col justify-between">

                    <div className="flex flex-col space-y-1.5">
                        <Label htmlFor="username">Username</Label>
                        <Input
                            id="username"
                            name="username"
                            placeholder="Enter your username"
                            value={formState.username}
                            onChange={handleChange}
                            disabled={current !== null}
                        />
                        {
                            statusState.errors.username &&
                            <div className="flex flex-col space-y-1.5 items-center text-red-600 text-sm">
                                <Label>{ statusState.errors.username}</Label>
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
                         statusState.errors.password &&
                        <div className="flex flex-col space-y-1.5 items-center text-red-600 text-sm">
                            <Label>{ statusState.errors.password}</Label>
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
                            disabled={current !== null}
                        />
                    </div>
                    {
                         statusState.errors.aName &&
                        <div className="flex flex-col space-y-1.5 items-center text-red-600 text-sm">
                            <Label>{ statusState.errors.aName}</Label>
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
                         statusState.errors.email &&
                        <div className="flex flex-col space-y-1.5 items-center text-red-600 text-sm">
                            <Label>{ statusState.errors.email}</Label>

                        </div>
                    }
                    <div className="flex flex-col space-y-1.5">
                        <Label htmlFor="role">Role</Label>
                        <Select
                            value={formState.role}
                            onValueChange={handleRoleChange}
                            disabled={current !== null}
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
                                onClick={ () => {
                                    void deleteUser()
                                }
                                }
                            >
                                Delete User
                            </Button>
                        )
                    }

                    { statusState.operationStatus && (
                        <div className={`mt-2 text-center ${ statusState.operationStatus.includes("Error") ? "text-red-500" : "text-green-500"}`}>
                            { statusState.operationStatus}
                        </div>
                    )}
                </form>
            </CardContent>
        </Card>
    );
};
