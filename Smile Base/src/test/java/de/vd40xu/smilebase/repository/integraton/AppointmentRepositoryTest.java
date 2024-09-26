package de.vd40xu.smilebase.repository.integraton;

import de.vd40xu.smilebase.model.Appointment;
import de.vd40xu.smilebase.model.Patient;
import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.model.emuns.AppointmentType;
import de.vd40xu.smilebase.model.emuns.UserRole;
import de.vd40xu.smilebase.repository.AppointmentRepository;
import de.vd40xu.smilebase.repository.PatientRepository;
import de.vd40xu.smilebase.repository.UserRepository;
import de.vd40xu.smilebase.repository.config.IntegrationRepositoryTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AppointmentRepositoryTest extends IntegrationRepositoryTest {

    @Autowired private AppointmentRepository appointmentRepository;

    @Autowired private PatientRepository patientRepository;

    @Autowired private UserRepository userRepository;

    private User testDoctor;
    private Patient testPatient;

    private final Clock clock = Clock.fixed(
        LocalDate.of(2024, 1, 8).atStartOfDay(ZoneId.systemDefault()).toInstant(),
        ZoneId.systemDefault()
    );

    @BeforeAll
    void setUp() {
        Patient patient = new Patient("Test Patient", LocalDate.now(), "INS-TEST", "Test Provider", "test@example.com");
        User doctor = User.builder()
                .username("doctor1")
                .password("password")
                .name("Dr. Smith")
                .email("dr.smith@example.com")
                .role(UserRole.DOCTOR)
                .active(true)
                .build();
        testDoctor = userRepository.save(doctor);
        testPatient = patientRepository.save(patient);
    }

    @Test
    @DisplayName("Integration > Find Appointments by Doctor ID")
    void test1() {
        Appointment appointment = new Appointment("New Check-up", LocalDateTime.now(clock), AppointmentType.QUICKCHECK);
        appointment.setDoctor(testDoctor);
        appointment.setPatient(testPatient);
        appointmentRepository.save(appointment);

        List<Appointment> result = appointmentRepository.findByDoctorId(testDoctor.getId());

        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(a -> a.getDoctor().getId().equals(testDoctor.getId())));
    }

    @Test
    @DisplayName("Integration > Find Appointments by Doctor ID and Date Range")
    void test2() {
        LocalDateTime now = LocalDateTime.now(clock);
        Appointment appointment = new Appointment("New Follow-up", now, AppointmentType.QUICKCHECK);
        appointment.setDoctor(testDoctor);
        appointment.setPatient(testPatient);
        appointmentRepository.save(appointment);

        List<Appointment> result = appointmentRepository.findByDoctorIdAndStartBetween(testDoctor.getId(), now.minusHours(1), now.plusHours(1));

        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(a -> a.getDoctor().getId().equals(testDoctor.getId()) && a.getStart().equals(now)));
    }

    @Test
    @DisplayName("Integration > Find Appointments by Patient ID")
    void test3() {
        Appointment appointment = new Appointment("New Annual check-up", LocalDateTime.now(clock), AppointmentType.QUICKCHECK);
        appointment.setDoctor(testDoctor);
        appointment.setPatient(testPatient);
        appointmentRepository.save(appointment);

        List<Appointment> result = appointmentRepository.findByPatientId(testPatient.getId());

        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(a -> a.getPatient().getId().equals(testPatient.getId())));
    }

    @Test
    @DisplayName("Integration > Find Appointments for Non-existent Doctor")
    void test4() {
        List<Appointment> result = appointmentRepository.findByDoctorId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Integration > Find Appointments for Non-existent Patient")
    void test5() {
        List<Appointment> result = appointmentRepository.findByPatientId(999L);

        assertTrue(result.isEmpty());
    }

}
