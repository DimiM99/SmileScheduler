import {format, parseISO} from 'date-fns'
import {Calendar as CalendarIcon, Loader2} from 'lucide-react'
import {cn, handleAsyncErrors} from "@/lib/utils"
import {Button} from "@/components/ui/button"
import {Calendar} from "@/components/ui/calendar"
import {
    Popover,
    PopoverContent,
    PopoverTrigger,
} from "@/components/ui/popover"
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card"
import {Badge} from "@/components/ui/badge"
import {useState, useEffect} from "react";
import {AppointmentResponse} from "@/models/services/responses/AppointmentResponse.ts";
import {PatientScheduleService} from "@/services/PatientScheduleService.ts";
import useQuery from "@/hooks/useQuery.ts";

const AppointmentCard = ({appointment}: { appointment: AppointmentResponse }) => (
    <Card className="mb-4 last:mb-0">
        <CardHeader>
            <CardTitle className="flex justify-between items-center">
                {appointment.title}
                <Badge>{appointment.appointmentType}</Badge>
            </CardTitle>
        </CardHeader>
        <CardContent>
            <p><strong>Date:</strong> {format (parseISO (appointment.start), 'PPP')}</p>
            <p>
                <strong>Time:</strong> {format (parseISO (appointment.start), 'p')} - {format (parseISO (appointment.end), 'p')}
            </p>
            <p><strong>Doctor:</strong> {appointment.doctor.name}</p>
        </CardContent>
    </Card>
)

export default function PatDashboard() {
    const [dateOfBirth, setDateOfBirth] = useState<Date> ()
    const [loading, setLoading] = useState (false)
    const [appointments, setAppointments] = useState<{
        nextAppointment: AppointmentResponse | null,
        upcomingAppointments: AppointmentResponse[]
    } | null> (null)
    const [showDateSelector, setShowDateSelector] = useState (true)
    const [currentPatientName, setCurrentPatientName] = useState<string> ("")
    const [patientID, setPatientID] = useState<number | null> (null)
    const [patientIDError, setPatientIDError] = useState<string | null> (null)

    const query = useQuery();

    const patientScheduleService = new PatientScheduleService ();
    const pID: string | null = query.get("pID");

    useEffect (() => {
        if (pID) {
            const parsedID = parseInt (pID, 10);
            if (!isNaN (parsedID)) {
                setPatientID (parsedID);
                setPatientIDError (null);
            } else {
                setPatientIDError ("Invalid patient ID provided.");
            }
        } else {
            setPatientIDError ("No patient ID provided.");
        }
    }, [pID]);

    const handleContinue = async () => {
        if (!dateOfBirth || patientID === null) return
        setLoading (true)
        setShowDateSelector (false)
        try {
            const result: AppointmentResponse[] = await patientScheduleService.fetchAppointments (dateOfBirth, patientID)

            const sortedAppointments = result.sort ((a, b) =>
                new Date (a.start).getTime () - new Date (b.start).getTime ()
            );

            if (sortedAppointments.length > 0) {
                setCurrentPatientName (sortedAppointments[0].patient.name);
            }

            const nextAppointment = sortedAppointments.length > 0 ? sortedAppointments[0] : null;
            const upcomingAppointments = sortedAppointments.slice (1);

            setAppointments ({
                nextAppointment,
                upcomingAppointments
            });
        } catch (error) {
            console.error ("Error fetching appointments:", error);
        } finally {
            setLoading (false)
        }
    }

    const onClick = handleAsyncErrors (async () => {
        await handleContinue ()
    })

    if (patientIDError) {
        return (
            <div className="container mx-auto p-4 min-h-screen flex flex-col items-center justify-center">
                <div className="text-center">
                    <h2 className="text-2xl font-bold mb-4">Error</h2>
                    <p className="text-red-500">{patientIDError}</p>
                    <p className="mt-4">Please check the URL and try again.</p>
                </div>
            </div>
        )
    }

    return (
        <div className="container mx-auto p-4 min-h-screen flex flex-col items-center justify-center">
            {showDateSelector ? (
                <div className="space-y-4 text-center">
                    <h2 className="text-2xl font-bold mb-4">Verify Your Birth Date to continue</h2>
                    <Popover>
                        <PopoverTrigger asChild>
                            <Button
                                variant={"outline"}
                                className={cn (
                                    "w-[280px] justify-start text-left font-normal",
                                    !dateOfBirth && "text-muted-foreground"
                                )}
                            >
                                <CalendarIcon className="mr-2 h-4 w-4"/>
                                {dateOfBirth ? format (dateOfBirth, "PPP") : <span>Pick a date</span>}
                            </Button>
                        </PopoverTrigger>
                        <PopoverContent className="w-auto p-0">
                            <Calendar
                                mode="single"
                                selected={dateOfBirth}
                                onSelect={setDateOfBirth}
                                initialFocus
                            />
                        </PopoverContent>
                    </Popover>
                    <Button onClick={onClick} disabled={!dateOfBirth || loading || patientID === null}
                            className="w-[280px]">
                        {loading ? (
                            <>
                                <Loader2 className="mr-2 h-4 w-4 animate-spin"/>
                                Loading
                            </>
                        ) : (
                            'Continue'
                        )}
                    </Button>
                </div>
            ) : loading ? (
                <div className="text-center">
                    <Loader2 className="h-8 w-8 animate-spin mx-auto"/>
                    <p className="mt-2">Loading appointments...</p>
                </div>
            ) : appointments ? (
                <div className="space-y-4 w-full max-w-2xl">
                    <h2 className="text-2xl font-bold mb-4">Appointments for {currentPatientName}</h2>
                    {appointments.nextAppointment && (
                        <div>
                            <h3 className="text-xl font-semibold mb-2">Next Appointment</h3>
                            <AppointmentCard appointment={appointments.nextAppointment}/>
                        </div>
                    )}
                    {appointments.upcomingAppointments.length > 0 && (
                        <div>
                            <h3 className="text-xl font-semibold mb-2">Upcoming Appointments</h3>
                            {appointments.upcomingAppointments.map ((appointment) => (
                                <AppointmentCard key={appointment.id} appointment={appointment}/>
                            ))}
                        </div>
                    )}
                </div>
            ) : null}
        </div>
    )
}

