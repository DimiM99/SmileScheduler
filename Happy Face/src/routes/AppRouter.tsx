import React from 'react';
import {BrowserRouter as Router, Route, Routes, Navigate} from 'react-router-dom';
import Login from '@/pages/Login';
import RecDashboard from "@/pages/RecDashboard.tsx";
import DocDashboard from "@/pages/DocDashboard.tsx";
import PatDashboard from "@/pages/PatDashboard.tsx";
import ProtectedRoute from './ProtectedRoute';
import AdminDashboard from "@/pages/AdminDashboard.tsx";

const AppRouter: React.FC = () => {
    return (
        <Router>
            <Routes>
                <Route path="/login" element={<Login/>}/>

                <Route path="/admin-dashboard" element={
                    <ProtectedRoute>
                        <AdminDashboard/>
                    </ProtectedRoute>
                }/>

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

                <Route path="/patient-schedule" element={
                    <PatDashboard/>
                }/>
                <Route path="*" element={<Navigate to="/login" replace/>}/>
            </Routes>
        </Router>
    );
};

export default AppRouter;
