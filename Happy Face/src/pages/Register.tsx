import React, {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {useAuth} from "@/hooks/ausAuth.ts";
import {User} from "lucide-react";

const Register: React.FC = () => {
    const [username, setUsername] = useState<string> ('');
    const [password, setPassword] = useState<string> ('');
    const {register} = useAuth ();
    const navigate = useNavigate ();

    const handleSubmit = async (e: React.FormEvent): Promise<void> => {
        e.preventDefault ();
        try {
            const user: User = {
                username,
                password,
                nam
            }
            await register (User);
            navigate ('/dashboard');
        } catch (error) {
            console.error ('Login failed:', error);
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <input
                type="text"
                value={username}
                onChange={(e) => {
                    setUsername (e.target.value);
                }}
                placeholder="Username"
            />
            <input
                type="password"
                value={password}
                onChange={(e) => {
                    setPassword (e.target.value);
                }}
                placeholder="Password"
            />
            <button type="submit">Login</button>
        </form>
    );
};

export default Login;
