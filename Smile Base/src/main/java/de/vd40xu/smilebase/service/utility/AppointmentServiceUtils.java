package de.vd40xu.smilebase.service.utility;

import de.vd40xu.smilebase.model.Appointment;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentServiceUtils {

    public AppointmentServiceUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static List<LocalDateTime> getFreeSlots(
            List<Appointment> appointments,
            long duration,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        List<LocalDateTime> allPossibleSlots = new ArrayList<>();
        LocalDateTime currentSlot = startDate;

        while (currentSlot.plusMinutes(duration).isBefore(endDate) || currentSlot.plusMinutes(duration).isEqual(endDate)) {
            if (
                currentSlot.toLocalTime().isAfter(LocalTime.of(7, 59))
                        &&
                currentSlot.toLocalTime().isBefore(LocalTime.of(17, 0))
            ) {
            allPossibleSlots.add(currentSlot);
        }
            currentSlot = currentSlot.plusMinutes(30);
        }

        return allPossibleSlots.stream()
                .filter(slot -> isSlotFree(slot, appointments, duration))
                .toList();
    }

    private static boolean isSlotFree(LocalDateTime slot, List<Appointment> existingAppointments, long appointmentDuration) {
        LocalDateTime slotEnd = slot.plusMinutes(appointmentDuration);
        return existingAppointments.stream()
                .noneMatch(appointment ->
                    (slot.isBefore(appointment.getEnd()) && appointment.getStart().isBefore(slotEnd)) ||
                    (slot.isEqual(appointment.getStart()) && slotEnd.isEqual(appointment.getEnd()))
                );
    }
}
