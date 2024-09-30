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
import {Appointment} from "@/models/Appointment";
interface AppointmentDetailCardProps {
    appointment: Appointment
    onClose: () => void;
}

export const AppointmentDetailCard: React.FC<AppointmentDetailCardProps> = ({
                                                                                appointment,
                                                                                onClose
                                                                            }) => {
    return (
        <Card className="w-full h-4/5 max-w-2xl p-6 shadow-lg bg-white rounded-lg">
            <CardHeader className="flex justify-between items-center border-b pb-4">
                <CardTitle className="text-2xl font-semibold text-gray-800">
                    {appointment.title}
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
                                <span className="font-semibold">Type:</span> {appointment.appointmentType}
                            </li>
                            <li>
                                <span className="font-semibold">Start:</span>{" "}
                                {format(new Date(appointment.start), "PPpp")}
                            </li>
                            <li>
                                <span className="font-semibold">End:</span>{" "}
                                {format(new Date(appointment.end), "PPpp")}
                            </li>
                            <li>
                                <span className="font-semibold">Doctor:</span> {appointment.doctor.name}
                            </li>
                        </ul>
                    </div>

                    <div>
                        <h3 className="text-lg font-medium text-gray-700">Patient Information</h3>
                        <ul className="mt-2 space-y-1">
                            <li>
                                <span className="font-semibold">Name:</span> {appointment.patient.name}
                            </li>
                            <li>
                                <span className="font-semibold">Medical History:</span>{" "}
                                {(appointment.patient.medicalHistory && appointment.patient.medicalHistory != "") ? appointment.patient.medicalHistory : "No Record"}
                            </li>
                            <li>
                                <span className="font-semibold">Allergies:</span> {(appointment.patient.allergies && appointment.patient.allergies != "") ? appointment.patient.allergies : "No Record"}
                            </li>
                        </ul>
                    </div>
                </div>

                <div className="mt-6">
                    <h3 className="text-lg font-medium text-gray-700">Appointment Reason</h3>
                    <p className="mt-2 text-gray-600">{
                        (appointment.reasonForAppointment && appointment.reasonForAppointment != "") ? appointment.reasonForAppointment : "No reason provided."
                    }</p>
                </div>

                <div className="mt-4">
                    <h3 className="text-lg font-medium text-gray-700">Notes</h3>
                    <p className="mt-2 text-gray-600">{
                        (appointment.notes && appointment.notes != "") ? appointment.notes : "No notes provided."
                    }</p>
                </div>
            </CardContent>

        </Card>
    );
};
