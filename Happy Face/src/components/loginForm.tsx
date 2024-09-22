import React, { ChangeEvent, FormEvent } from 'react';
import { useForm } from 'react-hook-form'; // Import useForm from react-hook-form
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/Form";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";

// Define the form schema and interface
interface LoginFormProps {
    credentials: {
        username: string;
        password: string;
    };
    onChange: (e: ChangeEvent<HTMLInputElement>) => void;
    onSubmit: (e: FormEvent) => Promise<void>;
    error: string | null;
}

export const LoginForm: React.FC<LoginFormProps> = ({ credentials, onChange, onSubmit, error }) => {
    const form = useForm(); // useForm hook to manage the form state

    return (
        <Card className="w-full max-w-md p-4 shadow-md">
            <CardHeader>
                <CardTitle className="text-center">
                    Login
                </CardTitle>
            </CardHeader>
            <CardContent>
                <Form {...form}>
                    <form
                        onSubmit={(e) => {
                            void onSubmit(e);
                        }}
                        className="space-y-8"
                    >
                        <div className="space-y-4">
                            <FormField
                                name="username"
                                render={() => (
                                    <FormItem>
                                        <FormLabel>Username</FormLabel>
                                        <FormControl>
                                            <Input
                                                type="text"
                                                name="username"
                                                value={credentials.username}
                                                onChange={onChange}
                                                placeholder="Username"
                                            />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                name="password"
                                render={() => (
                                    <FormItem>
                                        <FormLabel>Password</FormLabel>
                                        <FormControl>
                                            <Input
                                                type="password"
                                                name="password"
                                                value={credentials.password}
                                                onChange={onChange}
                                                placeholder="Password"
                                            />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                        </div>
                        {error && <p className="error">{error}</p>}
                        <Button type="submit" className="w-full">Login</Button>
                    </form>
                </Form>
            </CardContent>
        </Card>
    );
};
