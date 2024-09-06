import {useState} from "react"
import {Card, CardContent} from "@/components/ui/card"
import {Button} from "@/components/ui/button"
import {Popover, PopoverContent, PopoverTrigger} from "@/components/ui/popover"
import {Calendar} from "@/components/ui/calendar"
import {ChevronLeft, ChevronRight, Calendar as CalendarIcon} from "lucide-react"
import {cn} from "@/lib/utils"
import {DraftEvent} from "@/models/week-calendar/DraftEvent.ts";
import {Event} from "@/models/week-calendar/Event.ts"

const weekDays = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"]

interface WeekCalendarProps {
    events?: Event[]
    draftEvent?: DraftEvent
}

export default function WeekCalendar({events = [], draftEvent}: WeekCalendarProps) {
    const [currentDate, setCurrentDate] = useState (new Date ())
    const [hoveredEvent, setHoveredEvent] = useState<string | null> (null)

    const getWeekDates = (date: Date) => {
        const week = []
        const firstDayOfWeek = new Date (date)
        firstDayOfWeek.setDate (date.getDate () - date.getDay () + 1)
        for (let i = 0; i < 5; i++) {
            const day = new Date (firstDayOfWeek)
            day.setDate (firstDayOfWeek.getDate () + i)
            week.push (day)
        }
        return week
    }

    const weekDates = getWeekDates (currentDate)
    const monthName = currentDate.toLocaleString ('default', {month: 'long'})
    const weekNumber = getWeekNumber (currentDate)

    const navigateWeek = (direction: 'prev' | 'next') => {
        const newDate = new Date (currentDate)
        newDate.setDate (currentDate.getDate () + (direction === 'next' ? 7 : -7))
        setCurrentDate (newDate)
    }

    const goToToday = () => {
        setCurrentDate (new Date ())
    }

    const calculateEventStyle = (event: Event | DraftEvent) => {
        const startY = (event.start - 8) * 64 // 32px per half hour, 2 half hours per hour
        const duration = event.end - event.start
        const height = duration * 64

        return {
            top: `${startY.toString()}px`,
            height: `${height.toString()}px`,
            left: '4px',
            right: '4px',
        }
    }

    const handleSelectWeek = (date: Date | undefined) => {
        if (date) {
            setCurrentDate (date)
        }
    }

    const isOverlapping = (event1: Event | DraftEvent, event2: Event | DraftEvent) => {
        return event1.day === event2.day &&
            ((event1.start < event2.end && event1.start >= event2.start) ||
                (event1.end > event2.start && event1.end <= event2.end) ||
                (event1.start <= event2.start && event1.end >= event2.end))
    }

    const isDraftEventValid = () => {
        if (!draftEvent) return true
        return !events.some (event => isOverlapping (event, draftEvent))
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
                    <div className="flex items-center gap-2">
                        <Button variant="outline" size="icon" onClick={() => { navigateWeek ('prev'); }}>
                            <ChevronLeft className="h-4 w-4"/>
                            <span className="sr-only">Previous week</span>
                        </Button>
                        <Button variant="outline" size="icon" onClick={() => { navigateWeek ('next'); }}>
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
                    <div className="sticky left-0 bg-background z-20">
                        <div className="h-14"></div>
                        {Array.from ({length: 19}, (_, i) => i / 2 + 8).map ((time, index) => (
                            <div key={time}
                                 className="h-8 flex items-center justify-end pr-2 text-sm text-muted-foreground">
                                {index % 2 === 0 && (
                                    <span>{Math.floor (time) % 12 || 12}{Math.floor (time) >= 12 ? 'pm' : 'am'}</span>
                                )}
                            </div>
                        ))}
                    </div>
                    {weekDays.map ((day, dayIndex) => (
                        <div key={day} className="relative">
                            <div
                                className="sticky top-0 bg-background z-10 h-14 flex flex-col items-center justify-center font-semibold border-b border-border">
                                <div className="text-foreground">{day}</div>
                                <div className="text-sm text-muted-foreground">
                                    {weekDates[dayIndex].getDate ()}
                                </div>
                            </div>
                            <div className="relative h-[608px]">
                                {Array.from ({length: 19}, (_, i) => i / 2 + 8).map ((time, index) => (
                                    <div
                                        key={time}
                                        className={cn (
                                            "absolute left-0 right-0",
                                            index % 2 === 0 ? "border-t border-border" : "border-t border-border border-dashed"
                                        )}
                                        style={{top: `${(index * 32).toString()}px`}}
                                    ></div>
                                ))}
                                {events
                                    .filter (event => event.day === dayIndex)
                                    .map (event => (
                                        <div
                                            key={event.id}
                                            className="absolute rounded-md p-1 text-xs overflow-hidden"
                                            style={{
                                                ...calculateEventStyle (event),
                                                backgroundColor: event.color,
                                                opacity: hoveredEvent === event.id ? 0.8 : 1,
                                                transition: 'opacity 0.3s ease',
                                                cursor: 'pointer',
                                            }}
                                            onMouseEnter={() => { setHoveredEvent (event.id); }}
                                            onMouseLeave={() => { setHoveredEvent (null); }}
                                        >
                                            <div className="font-semibold text-foreground">{event.title}</div>
                                            <div
                                                className="text-muted-foreground">{`${formatTime (event.start)} - ${formatTime (event.end)}`}</div>
                                        </div>
                                    ))}
                                {draftEvent && draftEvent.day === dayIndex && (
                                    <div
                                        className="absolute rounded-md p-1 text-xs overflow-hidden border-2 border-dashed"
                                        style={{
                                            ...calculateEventStyle (draftEvent),
                                            backgroundColor: isDraftEventValid () ? 'rgba(0, 255, 0, 0.2)' : 'rgba(255, 0, 0, 0.2)',
                                            borderColor: isDraftEventValid () ? 'green' : 'red',
                                        }}
                                    >
                                        <div className="font-semibold text-foreground">{draftEvent.title}</div>
                                        <div
                                            className="text-muted-foreground">{`${formatTime (draftEvent.start)} - ${formatTime (draftEvent.end)}`}</div>
                                    </div>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            </CardContent>
        </Card>
    )
}

function formatTime(time: number): string {
    const hours = Math.floor (time)
    const minutes = Math.round ((time - hours) * 60)
    const ampm = hours >= 12 ? 'PM' : 'AM'
    const formattedHours = hours % 12 || 12
    return `${formattedHours.toString()}:${minutes.toString().padStart(2, '0')} ${ampm}`
}

function getWeekNumber(date: Date): number {
    const d = new Date (Date.UTC (date.getFullYear (), date.getMonth (), date.getDate ()))
    const dayNum = d.getUTCDay () || 7
    d.setUTCDate (d.getUTCDate () + 4 - dayNum)
    const yearStart = new Date (Date.UTC (d.getUTCFullYear (), 0, 1))
    return Math.ceil ((((d.getTime () - yearStart.getTime ()) / 86400000) + 1) / 7)
}