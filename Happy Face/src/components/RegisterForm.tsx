import React, {useEffect, useState} from 'react';
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { User } from "@/models/User";
import { Role } from "@/models/enums/Role";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {EmailAddress} from "@/classes/EmailAdress.ts";
export const RegisterForm: React.FC = () => {
    const [username, setUsername] = useState<string>('');
    const [password, setPassword] = useState<string>('');
    const [name, setName] = useState<string>('');
    const [email, setEmail] = useState<EmailAddress | string>('');
    const [role, setRole] = useState<Role>(Role.PATIENT);
    const [errorMessage, setErrorMessage] = useState<string>('');

    useEffect(() => {

    }, [errorMessage]);


    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            if (!username || !password || !name || !email) {
                setErrorMessage('All fields are required');
                return;
            }
            if (typeof email === 'string') {
                setEmail(EmailAddress.create(email).getValue());

            }

            const user: User = {
                username,
                password,
                name,
                email: email as EmailAddress,
                role
            };
            console.log(user);
        } catch (error) {
            console.error('Registration failed:', error);
            setErrorMessage(error.message);
        }
    };


    return (
        <Card className="w-full max-w-lg p-4 shadow-md"> {/* ShadCN card */}
            <CardHeader>
                <CardTitle className="text-center">Register</CardTitle> {/* Card title */}
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
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                            />
                        </div>
                        <div className="flex flex-col space-y-1.5">
                            <Label htmlFor="password">Password</Label>
                            <Input
                                id="password"
                                name="password"
                                type="password"
                                placeholder="Enter your password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                            />
                        </div>
                        <div className="flex flex-col space-y-1.5">
                            <Label htmlFor="name">Name</Label>
                            <Input
                                id="name"
                                name="name"
                                placeholder="Enter your full name"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                            />
                        </div>
                        <div className="flex flex-col space-y-1.5">
                            <Label htmlFor="email">Email</Label>
                            <Input
                                id="email"
                                name="email"
                                type="email"
                                placeholder="Enter your email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                            />
                        </div>
                        <div className="flex flex-col space-y-1.5">
                            <Label htmlFor="role">Role</Label>
                            <Select
                                value={role}
                                onValueChange={(e) =>  setRole(e as Role)}
                            >
                                <SelectTrigger id="role">
                                    <SelectValue placeholder="Select a role" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value={Role.PATIENT}>Patient</SelectItem>
                                    <SelectItem value={Role.DOCTOR}>Doctor</SelectItem>
                                    <SelectItem value={Role.RECEPTIONIST}>Receptionist</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>
                        <div className="flex flex-col space-y-1.5 items-center text-red-600">
                            <Label >{errorMessage}</Label>
                        </div>
                    </div>
                    <Button className="mt-4 w-full" type="submit">
                        Register
                    </Button>
                    <Button variant="outline" className="mt-4 w-full" asChild>
                        <a href="/login">Login</a>
                    </Button>
                </form>
            </CardContent>
        </Card>
    );
};
