import {useEffect, useState} from "react"
import {Card, CardContent} from "@/components/ui/card"
import {Button} from "@/components/ui/button"
import {Popover, PopoverContent, PopoverTrigger} from "@/components/ui/popover"
import {Calendar} from "@/components/ui/calendar"
import {ChevronLeft, ChevronRight, Calendar as CalendarIcon} from "lucide-react"
import {cn} from "@/lib/utils"
import {
    Select,
    SelectContent,
    SelectGroup,
    SelectItem,
    SelectLabel,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"
import {AppointmentResponse} from "@/models/services/responses/AppointmentResponse.ts";
import {Doctor} from "@/models";

const weekDays = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"]

interface WeekCalendarProps {
    events: AppointmentResponse[]
    isBookingMode: boolean
    onDoctorChange: (doctor: Doctor) => Promise<void>
    onDateChange: (date: Date) => Promise<void>
    onEventSelect: (event: AppointmentResponse) => void
    doctors: Doctor[]
    selectedDoctor: Doctor | null
    currentlySelectedDate: Date
}

export default function WeekCalendar({
    events,
    isBookingMode,
    onDoctorChange,
    onDateChange,
    onEventSelect,
    doctors,
    selectedDoctor,
    currentlySelectedDate,
}: WeekCalendarProps) {
    const [currentDate, setCurrentDate] = useState(currentlySelectedDate)
    const [hoveredEvent, setHoveredEvent] = useState<number | null>(null)

    useEffect(() => {
        setCurrentDate(currentlySelectedDate);
    }, [currentlySelectedDate]);

    const getWeekDates = (date: Date) => {
        const week = []
        const firstDayOfWeek = new Date(date)
        firstDayOfWeek.setDate(date.getDate() - date.getDay() + 1)
        for (let i = 0; i < 5; i++) {
            const day = new Date(firstDayOfWeek)
            day.setDate(firstDayOfWeek.getDate() + i)
            week.push(day)
        }
        return week
    }

    const weekDates = getWeekDates(currentDate)
    const monthName = currentDate.toLocaleString('default', { month: 'long' })
    const weekNumber = getWeekNumber(currentDate)

    const navigateWeek = (direction: 'prev' | 'next') => {
        const newDate = new Date(currentDate)
        newDate.setDate(currentDate.getDate() + (direction === 'next' ? 7 : -7))
        setCurrentDate(newDate)
        // eslint-disable-next-line @typescript-eslint/no-floating-promises
        onDateChange(newDate);
    }

    const goToToday = () => {
        const today = new Date()
        setCurrentDate(today)
        // eslint-disable-next-line @typescript-eslint/no-floating-promises
        onDateChange(today)
    }

    const calculateEventStyle = (event: AppointmentResponse, dayDate: Date) => {
        const startTime = new Date(event.start)
        startTime.setFullYear(dayDate.getFullYear(), dayDate.getMonth(), dayDate.getDate())
        const endTime = new Date(event.end)
        endTime.setFullYear(dayDate.getFullYear(), dayDate.getMonth(), dayDate.getDate())

        const startY = (startTime.getHours() + startTime.getMinutes() / 60 - 8) * 64
        const endY = (endTime.getHours() + endTime.getMinutes() / 60 - 8) * 64
        const height = endY - startY

        return {
            top: `${startY.toString()}px`,
            height: `${height.toString()}px`,
            left: '4px',
            right: '4px',
        }
    }

    const handleSelectWeek = (date: Date | undefined) => {
        if (date) {
            setCurrentDate(date)
            // eslint-disable-next-line @typescript-eslint/no-floating-promises
            onDateChange(date)
        }
    }

    const isSameDay = (date1: Date, date2: Date) => {
        return date1.getFullYear() === date2.getFullYear() &&
            date1.getMonth() === date2.getMonth() &&
            date1.getDate() === date2.getDate()
    }

    return (
        <Card className="w-full max-w-4xl mx-auto">
            <CardContent className="p-6">
                <div className="flex justify-between items-center mb-4">
                    <div className="flex items-center gap-2">
                        <CalendarIcon className="h-5 w-5 text-muted-foreground"/>
                        <span className="font-semibold text-foreground">
                            {monthName} - Week {weekNumber}
                        </span>
                    </div>
                    <div>
                        <DoctorAppointmentSelector 
                            doctors={doctors}
                            selectedDoctor={selectedDoctor}
                            // eslint-disable-next-line @typescript-eslint/no-misused-promises
                            onDoctorChange={onDoctorChange}
                        />
                    </div>
                    <div className="flex items-center gap-2">
                        <Button variant="outline" size="icon" onClick={() => { navigateWeek('prev'); }}>
                            <ChevronLeft className="h-4 w-4"/>
                            <span className="sr-only">Previous week</span>
                        </Button>
                        <Button variant="outline" size="icon" onClick={() => { navigateWeek('next'); }}>
                            <ChevronRight className="h-4 w-4"/>
                            <span className="sr-only">Next week</span>
                        </Button>
                        <Button variant="outline" size="sm" onClick={goToToday}>
                            Today
                        </Button>
                        <Popover>
                            <PopoverTrigger asChild>
                                <Button variant="outline" size="icon">
                                    <CalendarIcon className="h-4 w-4"/>
                                    <span className="sr-only">Open calendar</span>
                                </Button>
                            </PopoverTrigger>
                            <PopoverContent className="w-auto p-0" align="end">
                                <Calendar
                                    mode="single"
                                    selected={currentDate}
                                    onSelect={handleSelectWeek}
                                    initialFocus
                                />
                            </PopoverContent>
                        </Popover>
                    </div>
                </div>
                <div className="grid grid-cols-[auto,1fr,1fr,1fr,1fr,1fr] gap-4 min-w-[600px] overflow-x-auto">
                    {/* Time column */}
                    <div className="sticky left-0 bg-background z-20">
                        <div className="h-14"></div>
                        {Array.from({ length: 19 }, (_, i) => i / 2 + 8).map((time, index) => (
                            <div key={time}
                                 className="h-8 flex items-center justify-end pr-2 text-sm text-muted-foreground">
                                {index % 2 === 0 && (
                                    <span>{Math.floor(time) % 12 || 12}{Math.floor(time) >= 12 ? 'pm' : 'am'}</span>
                                )}
                            </div>
                        ))}
                    </div>
                    {/* Days columns */}
                    {weekDates.map((dayDate, dayIndex) => (
                        <div key={dayDate.toISOString()} className="relative">
                            <div className="sticky top-0 bg-background z-10 h-14 flex flex-col items-center justify-center font-semibold border-b border-border">
                                <div className="text-foreground">{weekDays[dayIndex]}</div>
                                <div className="text-sm text-muted-foreground">
                                    {dayDate.getDate()}
                                </div>
                            </div>
                            <div className="relative h-[608px]">
                                {Array.from({ length: 19 }, (_, i) => i / 2 + 8).map((time, index) => (
                                    <div
                                        key={time}
                                        className={cn(
                                            "absolute left-0 right-0",
                                            index % 2 === 0 ? "border-t border-border" : "border-t border-border border-dashed"
                                        )}
                                        style={{ top: `${(index * 32).toString()}px` }}
                                    ></div>
                                ))}
                                {events
                                    .filter(event => isSameDay(new Date(event.start), dayDate))
                                    .map(event => (
                                        <div
                                            key={event.id}
                                            className={cn(
                                                "absolute rounded-md p-1 text-xs overflow-hidden",
                                                isBookingMode ? "border border-dotted border-green-500" : "border border-solid border-blue-500"
                                            )}
                                            style={{
                                                ...calculateEventStyle(event, dayDate),
                                                backgroundColor: isBookingMode ? 'rgba(0, 255, 0, 0.1)' : 'rgba(59, 130, 246, 0.5)',
                                                opacity: hoveredEvent === event.id ? 0.8 : 1,
                                                transition: 'opacity 0.3s ease',
                                                cursor: 'pointer',
                                            }}
                                            onMouseEnter={() => { setHoveredEvent(event.id); }}
                                            onMouseLeave={() => { setHoveredEvent(null); }}
                                            onClick={() => { onEventSelect(event); }}
                                        >
                                            <div className="font-semibold text-foreground">{event.title}</div>
                                            <div className="text-muted-foreground">
                                                {`${formatTime(new Date(event.start))} - ${formatTime(new Date(event.end))}`}
                                            </div>
                                        </div>
                                    ))}
                            </div>
                        </div>
                    ))}
                </div>
            </CardContent>
        </Card>
    )
}

function formatTime(date: Date): string {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

function getWeekNumber(date: Date): number {
    const d = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()))
    const dayNum = d.getUTCDay() || 7
    d.setUTCDate(d.getUTCDate() + 4 - dayNum)
    const yearStart = new Date(Date.UTC(d.getUTCFullYear(), 0, 1))
    return Math.ceil((((d.getTime() - yearStart.getTime()) / 86400000) + 1) / 7)
}

interface DoctorAppointmentSelectorProps {
    doctors: Doctor[]
    selectedDoctor: Doctor | null
    onDoctorChange: (doctor: Doctor) => void
}

function DoctorAppointmentSelector({ doctors, selectedDoctor, onDoctorChange }: DoctorAppointmentSelectorProps) {
    if (doctors.length === 0) {
        return null
    } else {
        return (
            // eslint-disable-next-line @typescript-eslint/no-floating-promises,@typescript-eslint/no-non-null-assertion
            <Select onValueChange={(value) => { onDoctorChange(doctors.find(d => d.id.toString() === value)!); }}>
                <SelectTrigger className="w-[180px]">
                    <SelectValue placeholder={selectedDoctor ? selectedDoctor.name : 'Select a doctor'}/>
                </SelectTrigger>
                <SelectContent>
                    <SelectGroup>
                        <SelectLabel>Doctors</SelectLabel>
                        {doctors.map((doctor) => (
                            <SelectItem key={doctor.id} value={doctor.id.toString()}>
                                {doctor.name}
                            </SelectItem>
                        ))}
                    </SelectGroup>
                </SelectContent>
            </Select>
        )
    }
}