import React from 'react';
import {AuthProvider} from './contexts/AuthContext';
import {TokenAuthProvider} from './contexts/TokenAuthContext';
import AppRouter from './routes/AppRouter';

const App: React.FC = () => {
    return (
        <AuthProvider>
            <TokenAuthProvider>
                <AppRouter/>
            </TokenAuthProvider>
        </AuthProvider>
    );
};

export default App;
