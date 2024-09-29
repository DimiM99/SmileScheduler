import React from "react";
import {
    Card,
    CardHeader,
    CardContent,
    CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { X } from "lucide-react";
import { format } from "date-fns";

interface AppointmentDetailCardProps {
    onClose: () => void;
}

export const AppointmentDetailCard: React.FC<AppointmentDetailCardProps> = ({
                                                                                onClose
                                                                            }) => {
    const mockData = {
        id: 21,
        title: "Routine Dental Cleaning",
        start: "2024-10-01T10:00:00",
        end: "2024-10-01T11:00:00",
        appointmentType: "Cleaning",
        status: "Scheduled",
        doctor: {
            id: 23,
            username: "doc",
            name: "Dr. Doc",
            email: "doc@example.com",
            role: "DOCTOR",
            active: true
        },
        patient: {
            id: 7,
            name: "Pat Pat2",
            birthdate: "2018-09-25",
            insuranceNumber: "123412341234",
            insuranceProvider: "AOK",
            email: "pat@example.com",
            phoneNumber: "+49 1234123",
            medicalHistory: "No known allergies. Previous fillings in upper molars.",
            allergies: "Penicillin",
        },
        reasonForAppointment: "Routine check-up and cleaning.",
        notes: "Patient prefers morning appointments. Use fluoride-free toothpaste if possible."
    }

    return (
        <Card className="w-full h-4/5 max-w-2xl p-6 shadow-lg bg-white rounded-lg">
            <CardHeader className="flex justify-between items-center border-b pb-4">
                <CardTitle className="text-2xl font-semibold text-gray-800">
                    {mockData.title}
                </CardTitle>

                <Button
                    variant="ghost"
                    size="icon"
                    onClick={onClose}
                    aria-label="Close"
                    className="text-gray-500 hover:text-gray-700"
                >
                    <X className="w-5 h-5" />
                </Button>
            </CardHeader>

            <CardContent className="mt-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                        <h3 className="text-lg font-medium text-gray-700">Appointment Details</h3>
                        <ul className="mt-2 space-y-1">
                            <li>
                                <span className="font-semibold">Type:</span> {mockData.appointmentType}
                            </li>
                            <li>
                                <span className="font-semibold">Start:</span>{" "}
                                {format(new Date(mockData.start), "PPpp")}
                            </li>
                            <li>
                                <span className="font-semibold">End:</span>{" "}
                                {format(new Date(mockData.end), "PPpp")}
                            </li>
                            <li>
                                <span className="font-semibold">Doctor:</span> {mockData.doctor.name}
                            </li>
                        </ul>
                    </div>

                    <div>
                        <h3 className="text-lg font-medium text-gray-700">Patient Information</h3>
                        <ul className="mt-2 space-y-1">
                            <li>
                                <span className="font-semibold">Name:</span> {mockData.patient.name}
                            </li>
                            <li>
                                <span className="font-semibold">Medical History:</span>{" "}
                                {mockData.patient.medicalHistory}
                            </li>
                            <li>
                                <span className="font-semibold">Allergies:</span> {mockData.patient.allergies}
                            </li>
                        </ul>
                    </div>
                </div>

                <div className="mt-6">
                    <h3 className="text-lg font-medium text-gray-700">Appointment Reason</h3>
                    <p className="mt-2 text-gray-600">{mockData.reasonForAppointment}</p>
                </div>

                <div className="mt-4">
                    <h3 className="text-lg font-medium text-gray-700">Notes</h3>
                    <p className="mt-2 text-gray-600">{mockData.notes}</p>
                </div>
            </CardContent>

        </Card>
    );
};
