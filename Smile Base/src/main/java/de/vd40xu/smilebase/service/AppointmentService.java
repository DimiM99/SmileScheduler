package de.vd40xu.smilebase.service;

import de.vd40xu.smilebase.dto.AppointmentDTO;
import de.vd40xu.smilebase.dto.NewAppointmentDTO;
import de.vd40xu.smilebase.model.Appointment;
import de.vd40xu.smilebase.model.Patient;
import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.model.emuns.AppointmentType;
import de.vd40xu.smilebase.model.emuns.UserRole;
import de.vd40xu.smilebase.repository.AppointmentRepository;
import de.vd40xu.smilebase.repository.PatientRepository;
import de.vd40xu.smilebase.repository.UserRepository;
import de.vd40xu.smilebase.service.interfaces.IAppointmentService;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static de.vd40xu.smilebase.service.utility.AppointmentServiceUtils.getFreeSlots;
import static de.vd40xu.smilebase.service.utility.AppointmentServiceUtils.isSlotFree;

@Service
public class AppointmentService implements IAppointmentService {

    private final LocalTime clinicOpenTime = LocalTime.of(8, 0);
    private final LocalTime clinicCloseTime = LocalTime.of(17, 0);

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            UserRepository userRepository,
            PatientRepository patientRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    public List<LocalDateTime> getAvailableAppointments(
            Long doctorId,
            LocalDate date,
            AppointmentType appointmentType,
            boolean weekView
    ) {
        var refDates = new Object() {
            LocalDateTime startDate = date.atTime(clinicOpenTime);
            LocalDateTime endDate = date.atTime(clinicCloseTime);
        };
        if (weekView) {
            refDates.startDate = refDates.startDate
                                            .isBefore(LocalDateTime.now(clock).plusDays(1)) ?
                                                LocalDateTime.now(clock) :
                                                refDates.startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            refDates.endDate = refDates.startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        }
        User doc = userRepository
                .findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("The id provided is not a doctor id"));
        if (!doc.getRole().equals(UserRole.DOCTOR)) { throw new IllegalArgumentException("The id provided is not a doctor id"); }
        var appointments = appointmentRepository.findByDoctorIdAndStartBetween(doc.getId(), refDates.startDate, refDates.endDate);
        return getFreeSlots(appointments, appointmentType.getDuration(), refDates.startDate, refDates.endDate);
    }

    @Override
    public Appointment scheduleAppointment(NewAppointmentDTO appointmentDTO) throws IllegalArgumentException {
        Appointment appointment = new Appointment(
                appointmentDTO.getTitle(),
                appointmentDTO.getStart(),
                appointmentDTO.getAppointmentType()
        );
        if (!(appointment.getStart().toLocalTime().isAfter(clinicOpenTime.minusMinutes(1)) && appointment.getEnd().toLocalTime().isBefore(clinicCloseTime.plusMinutes(1)))) {
            throw new IllegalArgumentException("Appointment time is outside clinic hours");
        }
        checkForDoctor(appointment, appointmentDTO.getDoctorId());
        if (appointmentDTO.getPatient().getId() != null) {
            appointment.setPatient(
                    patientRepository.findById(appointmentDTO.getPatient().getId()).orElseThrow(
                            () -> new IllegalArgumentException("Patient not found")
                    )
            );
        } else {
            appointment.setPatient(
            patientRepository.save(
                new Patient(
                        appointmentDTO.getPatient().getName(),
                        appointmentDTO.getPatient().getBirthdate(),
                        appointmentDTO.getPatient().getInsuranceNumber(),
                        appointmentDTO.getPatient().getInsuranceProvider(),
                        appointmentDTO.getPatient().getEmail()
                )
            )
        );
        }
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElseThrow();
    }

    @Override
    public Appointment updateAppointment(AppointmentDTO appointmentDTO) {
        Appointment appointment = appointmentRepository.findById(appointmentDTO.getId()).orElseThrow();
        if (appointmentDTO.getTitle() != null) {
            appointment.setTitle(appointmentDTO.getTitle());
        }
        if (appointmentDTO.getStart() != null) {
            appointment.setStart(appointmentDTO.getStart());
            appointment.setEnd(appointment.getStart().plusMinutes(appointment.getAppointmentType().getDuration()));
        }
        if (appointmentDTO.getAppointmentType() != null) {
            appointment.setAppointmentType(appointmentDTO.getAppointmentType());
            appointment.setEnd(appointment.getStart().plusMinutes(appointment.getAppointmentType().getDuration()));
        }
        if (appointmentDTO.getDoctorId() != null) {
            checkForDoctor(appointment, appointmentDTO.getDoctorId());
        }
        return appointmentRepository.save(appointment);
    }

    @Override
    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    @Setter
    private Clock clock = Clock.systemDefaultZone();

    private void checkForDoctor(Appointment appointment, Long doctorId) {
        userRepository.findById(doctorId).ifPresentOrElse(
                doc -> {
                    if (doc.getRole().equals(UserRole.DOCTOR)) {
                        if (isSlotFree(
                                appointment.getStart(),
                                appointmentRepository.findByDoctorIdAndStartBetween(
                                        doc.getId(), appointment.getStart(), appointment.getEnd()
                                ).stream().filter(
                                        match -> !match.getId().equals(appointment.getId())
                                ).toList(),
                                appointment.getAppointmentType().getDuration()
                        )) {
                            appointment.setDoctor(doc);
                        } else {
                            throw new IllegalArgumentException("Doctor is not free at the requested time");
                        }
                    } else {
                        throw new IllegalArgumentException("User is not a doctor");
                    }
                },
                () -> {
                    throw new IllegalArgumentException("Doctor not found");
                }
        );
    }

}
