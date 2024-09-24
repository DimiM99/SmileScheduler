package de.vd40xu.smilebase.repository;

import de.vd40xu.smilebase.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    public List<Appointment> findByDoctorId(Long doctorId);
    public List<Appointment> findByDoctorIdAndStartBetween(Long doctorId, LocalDateTime start, LocalDateTime end);
    public List<Appointment> findByPatientId(Long patientId);
}
