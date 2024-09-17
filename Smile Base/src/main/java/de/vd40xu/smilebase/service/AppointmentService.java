package de.vd40xu.smilebase.service;

import de.vd40xu.smilebase.dto.AppointmentDTO;
import de.vd40xu.smilebase.model.Appointment;
import de.vd40xu.smilebase.model.emuns.AppointmentType;
import de.vd40xu.smilebase.repository.AppointmentRepository;
import de.vd40xu.smilebase.service.interfaces.IAppointmentService;
import lombok.Setter;

import java.time.*;
import java.util.List;

public class AppointmentService implements IAppointmentService {

    private final LocalTime clinicOpenTime = LocalTime.of(8, 0);
    private final LocalTime clinicCloseTime = LocalTime.of(17, 0);

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository
    ) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public List<LocalDateTime> getAvailableAppointments(
            Long doctorId,
            LocalDate date,
            AppointmentType appointmentType,
            boolean weekView
    ) {}

    @Override
    public Appointment scheduleAppointment(AppointmentDTO appointmentDTO) {
        return null;
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        return null;
    }

    @Override
    public Appointment updateAppointment(Appointment appointment) {
        return null;
    }

    @Override
    public void deleteAppointment(Long id) {}

    @Setter
    private Clock clock = Clock.systemDefaultZone();

}
