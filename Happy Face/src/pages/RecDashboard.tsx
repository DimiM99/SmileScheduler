import React, {useEffect, useState} from 'react';
import {useAuth} from "@/hooks/useAuth.ts";
import Layout from "@/components/layout.tsx";

const RecDashboard: React.FC = () => {
    const {user} = useAuth ();

    const [loading, setLoading] = useState<boolean>(true);

    useEffect (() => {
        setLoading(true);
        setLoading(false);
    }, [user]);

    if (loading) {
        return <p>Loading...</p>;
    }

    if (!user) {
        return <p>User not authenticated.</p>;
    }

    return (
        <Layout
            user={user}
            left={
                <div>
                    <h2>Week Preview</h2>
                    <p>This is the content for the left section.</p>
                </div>
            }
            right={
                <div>
                    <h2>Appointment Configuration</h2>
                    <p>This is the content for the right section.</p>
                </div>
            }
            leftWeight={3}
            rightWeight={2}
        />
    );
};

export default RecDashboard;
