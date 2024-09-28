import React from 'react';
import {Button} from "@/components/ui/button";
import {Input} from "@/components/ui/input";
import {Label} from "@/components/ui/label";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select";
import {Role} from "@/models/enums/Role";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {useState} from "react";
import {User} from "@/models";

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
    current: User | null
}


export const UserManagementForm: React.FC<FormProps> = (user) => {

    const [formState, setFormState] = useState<formData>({
        username: user.current?.username ? user.current.username : "",
        password: "",
        aName: user.current?.name ? user.current.name : "",
        email: user.current?.email ? user.current.email : "",
        role: user.current?.role ? user.current.role : Role.RECEPTIONIST,
    });


    const [errors, setErrors] = useState<ErrorMessages>({} as ErrorMessages)

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


    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        console.log(containsForbidden("´"))
        if (isFormValid()) {
            console.log(" Valid ")
            if (user.current) {

                console.log("Edit user")
                return
            }
            console.log("Create user")

        }
    };


    return (
        <Card className="w-full h-5/7 max-w-md p-4 shadow-md"> {/* ShadCN card */}
            <CardHeader>
                <CardTitle className="text-center">
                    {
                        user.current ? "Edit User" : "Create User"
                    }
                </CardTitle> {/* Card title */}
            </CardHeader>
            <CardContent>
                <form onSubmit={handleSubmit}>
                    <div className="grid w-full items-center gap-4">
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
                                <div className="flex flex-col space-y-1.5 items-center text-red-600">
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
                            <div className="flex flex-col space-y-1.5 items-center text-red-600">
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
                            <div className="flex flex-col space-y-1.5 items-center text-red-600">
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
                            <div className="flex flex-col space-y-1.5 items-center text-red-600">
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

                    </div>
                    <Button className="mt-10 w-full" type="submit">
                        {
                            user.current ? "Apply Changes" : "Create User"
                        }
                    </Button>
                </form>
            </CardContent>
        </Card>
    );
};
