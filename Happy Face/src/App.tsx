import React from 'react';
import {AuthProvider} from './contexts/AuthContext';
import AppRouter from './routes/AppRouter';

const App: React.FC = () => {
    return (
        <AuthProvider>
            <AppRouter/>
        </AuthProvider>
    );
};

export default App;
