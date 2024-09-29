import {format, parseISO, startOfYear, eachMonthOfInterval, getDaysInMonth, addMonths} from 'date-fns'
import {Loader2} from 'lucide-react'
import {handleAsyncErrors} from "@/lib/utils"
import {Button} from "@/components/ui/button"
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card"
import {Badge} from "@/components/ui/badge"
import {useState, useEffect} from "react";
import {AppointmentResponse} from "@/models/services/responses/AppointmentResponse.ts";
import {PatientScheduleService} from "@/services/PatientScheduleService.ts";
import useQuery from "@/hooks/useQuery.ts";
import {AxiosError} from "axios";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"

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
    const [year, setYear] = useState<number>(new Date().getFullYear())
    const [month, setMonth] = useState<number>(new Date().getMonth())
    const [day, setDay] = useState<number>(new Date().getDate())
    const [loading, setLoading] = useState (false)
    const [appointments, setAppointments] = useState<{
        nextAppointment: AppointmentResponse | null,
        upcomingAppointments: AppointmentResponse[]
    } | null> (null)
    const [showDateSelector, setShowDateSelector] = useState (true)
    const [currentPatientName, setCurrentPatientName] = useState<string> ("")
    const [patientID, setPatientID] = useState<number | null> (null)
    const [error, setError] = useState<string | null> (null)

    const years = Array.from({ length: 100 }, (_, i) => year - i)
    const months = eachMonthOfInterval({
        start: startOfYear(new Date(year, 0, 1)),
        end: addMonths(startOfYear(new Date(year, 0, 1)), 11)
    })
    const days = Array.from({ length: getDaysInMonth(new Date(year, month)) }, (_, i) => i + 1)

    const query = useQuery ();

    const patientScheduleService = new PatientScheduleService ();
    const pID: string | null = query.get ("pID");

    useEffect (() => {
        if (pID) {
            const parsedID = parseInt (pID, 10);
            if (!isNaN (parsedID)) {
                setPatientID (parsedID);
                setError (null);
            } else {
                setError ("Invalid patient ID provided.");
            }
        } else {
            setError ("No patient ID provided.");
        }
    }, [pID]);

    const handleContinue = async () => {
        const selectedDate = new Date(year, month, day)
        if (patientID === null) return
        setLoading (true)
        setShowDateSelector (false)
        try {
            const result: AppointmentResponse[] = await patientScheduleService.fetchAppointments (selectedDate, patientID)

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
        } catch (reqError) {
            console.error ("Error fetching appointments:", reqError);
            console.log (reqError)
            if (reqError instanceof AxiosError) {
                // this is an inescapable error can't be fixed
                // eslint-disable-next-line @typescript-eslint/no-unsafe-argument
                setError (reqError.response?.data || "An error occurred while fetching appointments.");
            }

        } finally {
            setLoading (false)
        }
    }

    const onClick = handleAsyncErrors (async () => {
        await handleContinue ()
    })

    if (error) {
        return (
            <div className="container mx-auto p-4 min-h-screen flex flex-col items-center justify-center">
                <Card className="text-center p-4">
                    <CardHeader>
                        <CardTitle className="text-2xl font-bold mb-4">Error</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <p className="text-red-500">{error}</p>
                        <p className="mt-4">Please try again. If the error persists please call us</p>
                        <Button onClick={() => {
                            window.location.reload ();
                        }} className="mt-4">Try again</Button>
                    </CardContent>
                </Card>
            </div>
        )
    }

    return (
        <div className="container mx-auto p-4 min-h-screen flex flex-col items-center justify-center">
            {showDateSelector ? (
                <div className="space-y-4 text-center">
                    <h2 className="text-2xl font-bold mb-4">Verify Your Birth Date to continue</h2>
                    <div className="flex flex-col sm:flex-row gap-4">
                        <Select value={year.toString ()} onValueChange={(value) => { setYear (parseInt (value)); }}>
                            <SelectTrigger className="w-[180px]">
                                <SelectValue placeholder="Year"/>
                            </SelectTrigger>
                            <SelectContent>
                                {years.map ((y) => (
                                    <SelectItem key={y} value={y.toString ()}>{y}</SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                        <Select value={month.toString ()} onValueChange={(value) => { setMonth (parseInt (value)); }}>
                            <SelectTrigger className="w-[180px]">
                                <SelectValue placeholder="Month"/>
                            </SelectTrigger>
                            <SelectContent>
                                {months.map ((m, index) => (
                                    <SelectItem key={index} value={index.toString ()}>{format (m, 'MMMM')}</SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                        <Select value={day.toString ()} onValueChange={(value) => { setDay (parseInt (value)); }}>
                            <SelectTrigger className="w-[180px]">
                                <SelectValue placeholder="Day"/>
                            </SelectTrigger>
                            <SelectContent>
                                {days.map ((d) => (
                                    <SelectItem key={d} value={d.toString ()}>{d}</SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </div>
                    <Button onClick={onClick} disabled={loading} className="w-[280px]">
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

