package de.vd40xu.smilebase.service.interfaces;

import de.vd40xu.smilebase.dto.AppointmentDTO;
import de.vd40xu.smilebase.dto.NewAppointmentDTO;
import de.vd40xu.smilebase.model.Appointment;
import de.vd40xu.smilebase.model.emuns.AppointmentType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IAppointmentService {
    List<LocalDateTime> getAvailableAppointments(Long doctorId, LocalDate date, AppointmentType appointmentType, boolean weekView);
    List<Appointment> getAppointmentsForDoctor(Long doctorId, LocalDate date, boolean weekView);
    Appointment scheduleAppointment(NewAppointmentDTO appointmentDTO);
    Appointment getAppointmentById(Long id);
    Appointment updateAppointment(AppointmentDTO appointment);
    void deleteAppointment(Long id);
}
