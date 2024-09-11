import React from 'react';
import {BrowserRouter as Router, Route, Routes, Navigate} from 'react-router-dom';
import Login from '../pages/Login';
import RecDashboard from "../pages/RecDashboard.tsx";
import DocDashboard from "../pages/DocDashboard.tsx";
import AppointmentView from "../pages/AppointmentView.tsx";
import ProtectedRoute from './ProtectedRoute';
import TokenProtectedRoute from './TokenProtectedRoute';

const AppRouter: React.FC = () => {
    return (
        <Router>
            <Routes>
                <Route path="/login" element={<Login/>}/>
                <Route path="/register" element={<Register/>}/>
                <Route path="/receptionist-dashboard" element={
                    <ProtectedRoute>
                        <RecDashboard/>
                    </ProtectedRoute>
                }/>
                <Route path="/doctor-dashboard" element={
                    <ProtectedRoute>
                        <DocDashboard/>
                    </ProtectedRoute>
                }/>
                <Route path="/appointments" element={
                    <TokenProtectedRoute>
                        <AppointmentView/>
                    </TokenProtectedRoute>
                }/>
                <Route path="*" element={<Navigate to="/login" replace/>}/>
            </Routes>
        </Router>
    );
};

export default AppRouter;
