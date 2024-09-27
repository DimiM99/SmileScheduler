package de.vd40xu.smilebase.service.integration;

import de.vd40xu.smilebase.dto.PatientScheduleRequestDTO;
import de.vd40xu.smilebase.model.Appointment;
import de.vd40xu.smilebase.model.Patient;
import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.model.emuns.AppointmentType;
import de.vd40xu.smilebase.model.emuns.UserRole;
import de.vd40xu.smilebase.repository.AppointmentRepository;
import de.vd40xu.smilebase.repository.PatientRepository;
import de.vd40xu.smilebase.repository.UserRepository;
import de.vd40xu.smilebase.service.PatientScheduleService;
import de.vd40xu.smilebase.service.config.AuthContextConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static de.vd40xu.smilebase.service.utility.PSUtility.generateHmacSha256;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PatientScheduleServiceTest extends AuthContextConfiguration {

    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private UserRepository userRepository;

    @Autowired private PatientScheduleService patientScheduleService;

    User testDoctor;
    Patient testPatient;
    Patient testPatient2;
    Appointment testAppointment;
    Appointment testAppointment2;

    String prepareToken() throws NoSuchAlgorithmException, InvalidKeyException {
        return generateHmacSha256("supertoken", "testsecret");
    }

    @BeforeAll
    void setup() {
        super.setUp();
        testDoctor = userRepository.save(
                new User(10L,
                        "doctorUser",
                        "password",
                        "John DOE",
                        "test@mail.com",
                        UserRole.DOCTOR,
                        true)
        );
        testPatient = patientRepository.save(
                new Patient( "Will Newman",
                        LocalDate.of(1990, 1, 1),
                        "123456789",
                        "AOK",
                        "test-pateint@email.com",
                        "123456")
        );
        testPatient2 = patientRepository.save(
                new Patient( "Jane Newman",
                        LocalDate.of(1990, 1, 2),
                        "123456888",
                        "AOK",
                        "test-patient2@mail.com",
                        "123456")
        );
        Appointment testAppointmentInit = new Appointment("Test Appointment",
                LocalDate.now().plusDays(3).atTime(10, 0),
                AppointmentType.QUICKCHECK
        );
        testAppointmentInit.setPatient(testPatient);
        testAppointmentInit.setDoctor(testDoctor);
        testAppointment = appointmentRepository.save(testAppointmentInit);
        Appointment testAppointment2Init = new Appointment("Test Appointment 2",
                LocalDate.now().atTime(11, 0),
                AppointmentType.QUICKCHECK
        );
        testAppointment2Init.setPatient(testPatient2);
        testAppointment2Init.setDoctor(testDoctor);
        testAppointment2 = appointmentRepository.save(testAppointment2Init);
    }

    @Test
    @DisplayName("Integration > get schedule for a patient")
    void test1() throws NoSuchAlgorithmException, InvalidKeyException {
        int expectedAppointments = 2;

        if (LocalDateTime.now().isBefore(testAppointment.getStart())) {
            expectedAppointments--;
        }

        PatientScheduleRequestDTO requestDTO = new PatientScheduleRequestDTO(
                prepareToken(),
                testPatient.getId(),
                testPatient.getBirthdate()
        );

        List<Appointment> appointments = patientScheduleService.getPatientSchedule(requestDTO);

        assertEquals(expectedAppointments, appointments.size());
        assertEquals(testAppointment.getId(), appointments.getFirst().getId());
    }

    @Test
    @DisplayName("Integration > get schedule for a patient (with wrong birthdate)")
    void test2() throws NoSuchAlgorithmException, InvalidKeyException {
        PatientScheduleRequestDTO requestDTO = new PatientScheduleRequestDTO(
                prepareToken(),
                testPatient.getId(),
                testPatient2.getBirthdate()
        );

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> patientScheduleService.getPatientSchedule(requestDTO));
        assertEquals("Patient date of birth does not match", e.getMessage());
    }

    @Test
    @DisplayName("Integration > get schedule for a patient that does not exist")
    void test3() throws NoSuchAlgorithmException, InvalidKeyException {
        PatientScheduleRequestDTO requestDTO = new PatientScheduleRequestDTO(
                prepareToken(),
                999L,
                testPatient.getBirthdate()
        );

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> patientScheduleService.getPatientSchedule(requestDTO));
        assertEquals("Patient not found", e.getMessage());
    }

    @Test
    @DisplayName("Integration > get schedule for a patient with invalid token")
    void test4(){
        PatientScheduleRequestDTO requestDTO = new PatientScheduleRequestDTO(
                "invalidtoken",
                testPatient.getId(),
                testPatient.getBirthdate()
        );

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> patientScheduleService.getPatientSchedule(requestDTO));
        assertEquals("Invalid token", e.getMessage());
    }
}