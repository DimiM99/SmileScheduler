import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { ChevronLeft, ChevronRight } from "lucide-react";

interface DayInfo {
  day: string;
  date: number;
  month: string;
}

const WeekCalendar: React.FC = () => {
  const days: string[] = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri'];
  const startHour = 8;
  const endHour = 17;
  const intervalMinutes = 30;

  const getTimeSlots = (): string[] => {
    const slots: string[] = [];
    for (let hour = startHour; hour < endHour; hour++) {
      for (let minute = 0; minute < 60; minute += intervalMinutes) {
        slots.push(`${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`);
      }
    }
    return slots;
  };

  const timeSlots = getTimeSlots();

  const getCurrentWeek = (): DayInfo[] => {
    const now = new Date();
    const dayOfWeek = now.getDay();
    const diff = dayOfWeek === 0 ? -6 : 1 - dayOfWeek; // Adjust to start from Monday
    const monday = new Date(now);
    monday.setDate(now.getDate() + diff);

    return days.map((day, index) => {
      const date = new Date(monday);
      date.setDate(monday.getDate() + index);
      return {
        day,
        date: date.getDate(),
        month: date.toLocaleString('default', { month: 'short' }),
      };
    });
  };

  const week: DayInfo[] = getCurrentWeek();

  return (
    <Card className="w-full h-full flex flex-col">
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2 flex-shrink-0">
        <CardTitle className="text-2xl font-bold">Work Week Calendar</CardTitle>
        <div className="flex space-x-2">
          <Button variant="outline" size="icon">
            <ChevronLeft className="h-4 w-4" />
          </Button>
          <Button variant="outline" size="icon">
            <ChevronRight className="h-4 w-4" />
          </Button>
        </div>
      </CardHeader>
      <CardContent className="flex-grow overflow-auto p-0">
        <div className="grid grid-cols-6 h-full">
          <div className="col-span-1 bg-gray-50 border-r border-gray-200">
            {timeSlots.map((slot, index) => (
              <div key={slot} className="h-8 flex items-center justify-end pr-2">
                {index % 2 === 0 && (
                  <span className="text-sm text-gray-500">{slot}</span>
                )}
              </div>
            ))}
          </div>
          {week.map(({ day, date, month }) => (
            <div key={day} className="border-r border-gray-200 last:border-r-0 relative">
              <div className="text-center py-2 border-b border-gray-200 sticky top-0 bg-white z-10">
                <div className="font-semibold">{day}</div>
                <div>{date}</div>
                <div className="text-sm text-gray-500">{month}</div>
              </div>
              <div className="relative">
                {timeSlots.map((slot, index) => (
                  <div
                    key={`${day}-${slot}`}
                    className="absolute w-full border-t border-gray-100"
                    style={{ top: `${index * 32}px` }}
                  ></div>
                ))}
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  );
};

export default WeekCalendar;
