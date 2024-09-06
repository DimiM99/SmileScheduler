import {Event} from "@/models/week-calendar/Event.ts";

export type DraftEvent = Omit<Event, 'id' | 'color'>