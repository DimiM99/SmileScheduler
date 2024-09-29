import React, { useState } from "react";
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table";
import {
    Card,
    CardContent,
    CardFooter,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {AppointmentResponse} from "@/models/services/responses/AppointmentResponse.ts";

interface AppointmentListProps {
    appointments: AppointmentResponse[];
    selectedAppointment: AppointmentResponse | null;
    onAppointmentSelect: (appointment: AppointmentResponse | null) => void;
}

export const AppointmentList: React.FC<AppointmentListProps> = ({
                                                                    appointments,
                                                                    selectedAppointment,
                                                                    onAppointmentSelect,
                                                                }) => {
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 12;

    const totalPages = Math.ceil(appointments.length / itemsPerPage);
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const currentPageAppointments = appointments.slice(startIndex, endIndex);

    const handleRowClick = (appointment: AppointmentResponse) => {
        if (selectedAppointment?.id === appointment.id) {
            onAppointmentSelect(null);
        } else {
            onAppointmentSelect(appointment);
        }
    };

    return (
        <Card className="w-full h-4/5 max-w-xl p-4 shadow-md">
            <CardHeader>
                <CardTitle className="text-center">Appointments</CardTitle>
            </CardHeader>

            <CardContent className="overflow-auto">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>Title</TableHead>
                            <TableHead>Start</TableHead>
                            <TableHead>Type</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {currentPageAppointments.map((appointment) => (
                            <TableRow
                                key={appointment.id}
                                onClick={() => {
                                    handleRowClick(appointment);
                                }}
                                className={
                                    selectedAppointment?.id === appointment.id
                                        ? "bg-gray-100"
                                        : ""
                                }
                                style={{ cursor: "pointer" }}
                            >
                                <TableCell>{appointment.title}</TableCell>
                                <TableCell>
                                    {new Date(appointment.start).toLocaleString()}
                                </TableCell>
                                <TableCell>{appointment.appointmentType}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </CardContent>

            {appointments.length > itemsPerPage && (
                <CardFooter className="flex justify-between items-center">
                    <div className="text-sm text-muted-foreground">
                        Page {currentPage} of {totalPages}
                    </div>
                    <div className="space-x-2">
                        <Button
                            size="sm"
                            onClick={() => {
                                setCurrentPage((prev) => Math.max(prev - 1, 1));
                            }}
                            disabled={currentPage === 1}
                        >
                            Previous
                        </Button>
                        <Button
                            size="sm"
                            onClick={() => {
                                setCurrentPage((prev) =>
                                    Math.min(prev + 1, totalPages)
                                );
                            }
                            }
                            disabled={currentPage === totalPages}
                        >
                            Next
                        </Button>
                    </div>
                </CardFooter>
            )}
        </Card>
    );
};
