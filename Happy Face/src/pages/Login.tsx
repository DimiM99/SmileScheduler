import React, {useState, useEffect} from 'react';
import {useNavigate} from 'react-router-dom';
import {useAuth} from "@/hooks/useAuth.ts";
import {LoginRequest} from "@/models/services/requests/LoginRequest.ts";
import {Role} from "@/models/enums/Role.ts";


const dashboardRoutes: Record<Role, string> = {
    [Role.RECEPTIONIST]: '/receptionist-dashboard',
    [Role.DOCTOR]: '/doctor-dashboard',
    [Role.ADMIN]: '/admin-dashboard',
};

const Login: React.FC = () => {
    const [credentials, setCredentials] = useState<LoginRequest>({
        username: '',
        password: '',
    });
    const [error, setError] = useState<string | null>(null);
    const { login, user } = useAuth();
    const navigate = useNavigate();


    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>): void => {
        const { name, value } = e.target;
        setCredentials((prevState) => ({ ...prevState, [name]: value }));
    };

    useEffect(() => {
        if (user) {
            const route = dashboardRoutes[user.role];
            if (route) {
                navigate(route);
            } else {
                setError('Unknown user role.');
            }
        }
    }, [user, navigate]);

    const handleSubmit = async (e: React.FormEvent): Promise<void> => {

        e.preventDefault ();
        setError(null);
        try {
            await login(credentials).then(() => {

            });

        } catch (err) {
            console.error('Login failed:', err);
            setError('Login failed. Please try again.');
        }
    };
    return (
        <form onSubmit={(e)=>{void handleSubmit(e)}}>
            <input
                type="text"
                name="username"
                value={credentials.username}
                onChange={handleInputChange}
                placeholder="Username"
            />
            <input
                type="password"
                name="password"
                value={credentials.password}
                onChange={handleInputChange}
                placeholder="Password"
            />
            <button type="submit">Login</button>
            {error && <p className="error">{error}</p>}
        </form>
    );
};

export default Login;
