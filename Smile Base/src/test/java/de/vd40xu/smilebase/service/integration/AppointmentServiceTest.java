package de.vd40xu.smilebase.service.integration;

import static org.junit.jupiter.api.Assertions.*;

import de.vd40xu.smilebase.dto.AppointmentDTO;
import de.vd40xu.smilebase.dto.NewAppointmentDTO;
import de.vd40xu.smilebase.dto.PatientDTO;
import de.vd40xu.smilebase.model.Appointment;
import de.vd40xu.smilebase.model.Patient;
import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.model.emuns.AppointmentType;
import de.vd40xu.smilebase.model.emuns.UserRole;
import de.vd40xu.smilebase.repository.AppointmentRepository;
import de.vd40xu.smilebase.repository.PatientRepository;
import de.vd40xu.smilebase.repository.UserRepository;
import de.vd40xu.smilebase.service.AppointmentService;
import de.vd40xu.smilebase.service.config.AuthContextConfiguration;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.*;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppointmentServiceTest extends AuthContextConfiguration {

    @Autowired private AppointmentService appointmentService;

    @Autowired private UserRepository userRepository;

    @Autowired private PatientRepository patientRepository;

    @Autowired private AppointmentRepository appointmentRepository;

    private List<User> doctors;
    private LocalDateTime startDate;
    private final Clock clock = Clock.fixed(Instant.parse("2023-06-15T10:00:00Z"), ZoneId.systemDefault());

    @BeforeAll
    public void setUp() {
        super.setUp();
        appointmentService.setClock(clock);
        startDate = LocalDateTime.now(clock).withHour(0).withMinute(0).withSecond(0).withNano(0);

        // Create 3 doctors
        doctors = IntStream.range(0, 3)
            .mapToObj(i -> userRepository.save(User.builder()
                .username("doctor" + i)
                .password("password")
                .name("Dr. Smith" + i)
                .email("dr.smith" + i + "@example.com")
                .role(UserRole.DOCTOR)
                .active(true)
                .build()))
            .toList();

        // Create 10 patients
        List<Patient> patients = IntStream.range(0, 10)
                .mapToObj(i -> patientRepository.save(new Patient(
                        "Patient" + i,
                        LocalDate.now(clock).minusYears(30 + i),
                        "INS" + i,
                        "Provider" + i,
                        "patient" + i + "@example.com"
                )))
                .toList();

        // Create appointments for the past week and next week
        LocalDateTime currentDate = startDate.minusDays(7);
        for (int day = 0; day < 14; day++) {
            for (User doctor : doctors) {
                for (int hour = 8; hour < 17; hour += 2) {
                    if (Math.random() < 0.7) { // 70% chance of creating an appointment
                        Patient patient = patients.get((int) (Math.random() * patients.size()));
                        AppointmentType type = Math.random() < 0.8 ? AppointmentType.QUICKCHECK : AppointmentType.EXTENSIVE;
                        Appointment appointment = new Appointment(
                            "Appointment",
                            currentDate.withHour(hour),
                            type
                        );
                        appointment.setDoctor(doctor);
                        appointment.setPatient(patient);
                        appointmentRepository.save(appointment);
                    }
                }
            }
            currentDate = currentDate.plusDays(1);
        }
    }


    @Test
    @DisplayName("Integration > Get Available Appointments")
    void test1() {
        LocalDate date = startDate.plusDays(1).toLocalDate();
        AppointmentType appointmentType = AppointmentType.QUICKCHECK;

        List<LocalDateTime> availableSlots = appointmentService.getAvailableAppointments(doctors.getFirst().getId(), date, appointmentType, false);

        assertFalse(availableSlots.isEmpty());
        assertTrue(availableSlots.stream().allMatch(slot -> slot.toLocalDate().equals(date)));
        assertTrue(availableSlots.stream().allMatch(slot -> slot.getHour() >= 8 && slot.getHour() < 17));
    }

    @Test
    @DisplayName("Integration > Get Available Appointments (Week View)")
    void test2() {
        LocalDate date = startDate.plusDays(1).toLocalDate();
        AppointmentType appointmentType = AppointmentType.QUICKCHECK;

        List<LocalDateTime> availableSlots = appointmentService.getAvailableAppointments(doctors.getFirst().getId(), date, appointmentType, true);

        assertFalse(availableSlots.isEmpty());
        assertTrue(availableSlots.stream().allMatch(slot -> slot.toLocalDate().getDayOfWeek().getValue() <= 5));
        assertTrue(availableSlots.stream().allMatch(slot -> slot.getHour() >= 8 && slot.getHour() < 17),
                   "All slots should be within work hours (8 AM to 5 PM)");
    }


    @Test
    @DisplayName("Integration > Schedule Appointment")
    void test3() {
        User doctor = doctors.getFirst();
        PatientDTO patientDTO = new PatientDTO(
            "New Patient",
            "INS-NEW",
            LocalDate.of(1990, 1, 1),
            "New Provider",
            "newpatient@example.com"
        );
        NewAppointmentDTO appointmentDTO = new NewAppointmentDTO(
            "New Test Appointment",
            doctor.getId(),
            startDate.plusDays(2).withHour(10),
            AppointmentType.QUICKCHECK,
            patientDTO
        );

        Appointment result = appointmentService.scheduleAppointment(appointmentDTO);

        assertNotNull(result);
        assertEquals("New Test Appointment", result.getTitle());
        assertEquals(doctor.getId(), result.getDoctor().getId());
        assertEquals(doctor.getName(), result.getDoctor().getName());
        assertEquals(doctor.getEmail(), result.getDoctor().getEmail());
        assertNotNull(result.getPatient());
        assertEquals("New Patient", result.getPatient().getName());
    }

    @Test
    @DisplayName("Integration > Update Appointment")
    void test4() {
        Appointment existingAppointment = appointmentRepository.findAll().getFirst();
        User newDoctor = doctors.get(1);

        AppointmentDTO appointmentDTO = new AppointmentDTO(
            existingAppointment.getId(),
            "Updated Appointment",
            existingAppointment.getPatient().getId(),
            newDoctor.getId(),
            existingAppointment.getStart().plusDays(1),
            AppointmentType.EXTENSIVE
        );

        Appointment result = appointmentService.updateAppointment(appointmentDTO);

        assertNotNull(result);
        assertEquals("Updated Appointment", result.getTitle());
        assertEquals(AppointmentType.EXTENSIVE, result.getAppointmentType());
        assertEquals(newDoctor.getId(), result.getDoctor().getId());
        assertEquals(newDoctor.getName(), result.getDoctor().getName());
        assertEquals(newDoctor.getEmail(), result.getDoctor().getEmail());

    }

    @Test
    @DisplayName("Integration > Delete Appointment")
    void test5() {
        Appointment appointmentToDelete = appointmentRepository.findAll().getFirst();
        Long appointmentId = appointmentToDelete.getId();

        appointmentService.deleteAppointment(appointmentId);

        assertThrows(Exception.class, () -> appointmentService.getAppointmentById(appointmentId));
    }
}
