export interface Event {
    id: string
    title: string
    day: number // 0-4 (Monday-Friday)
    start: number // 24-hour format float (e.g., 9.5 for 9:30 AM)
    end: number // 24-hour format float (e.g., 17 for 5:00 PM)
    color: string
}