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
import java.util.Objects;
import java.util.Optional;
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
    public void setUpTestEnv() {
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

    private List<LocalDateTime> getFreeSlots(Long docId, LocalDate date, AppointmentType appointmentType, boolean weekView) {
        return appointmentService.getAvailableAppointments(docId, date, appointmentType, weekView);
    }


    @Test
    @DisplayName("Integration > Get Available Appointments")
    void test1() {
        LocalDate date = startDate.plusDays(1).toLocalDate();
        AppointmentType appointmentType = AppointmentType.QUICKCHECK;

        List<LocalDateTime> availableSlots = getFreeSlots(doctors.getFirst().getId(), date, appointmentType, false);

        assertFalse(availableSlots.isEmpty());
        assertTrue(availableSlots.stream().allMatch(slot -> slot.toLocalDate().equals(date)));
        assertTrue(availableSlots.stream().allMatch(slot -> slot.getHour() >= 8 && slot.getHour() < 17));
    }

    @Test
    @DisplayName("Integration > Get Available Appointments (Week View)")
    void test2() {
        LocalDate date = startDate.plusDays(1).toLocalDate();
        AppointmentType appointmentType = AppointmentType.QUICKCHECK;

        List<LocalDateTime> availableSlots = getFreeSlots(doctors.getFirst().getId(), date, appointmentType, true);

        assertFalse(availableSlots.isEmpty());
        assertTrue(availableSlots.stream().allMatch(slot -> slot.toLocalDate().getDayOfWeek().getValue() <= 5));
        assertTrue(availableSlots.stream().allMatch(slot -> slot.getHour() >= 8 && slot.getHour() < 17),
                   "All slots should be within work hours (8 AM to 5 PM)");
    }


    @Test
    @DisplayName("Integration > Schedule Appointment")
    void test3() {
        User doctor = doctors.getFirst();
        LocalDateTime requestTime = LocalDate.now(clock).with(DayOfWeek.WEDNESDAY).atTime(13, 30);
        LocalDateTime freeSlot = getFreeSlots(
                doctor.getId(),
                requestTime.toLocalDate(),
                AppointmentType.QUICKCHECK,
                false
        ).getFirst();
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
            freeSlot,
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
        LocalDateTime requestTime = existingAppointment.getStart();
        LocalDateTime freeSlot = getFreeSlots(
                newDoctor.getId(),
                requestTime.toLocalDate(),
                AppointmentType.EXTENSIVE,
                false
        ).getFirst();

        AppointmentDTO appointmentDTO = new AppointmentDTO(
            existingAppointment.getId(),
            "Updated Appointment",
            existingAppointment.getPatient().getId(),
            newDoctor.getId(),
            freeSlot,
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

    @Test
    @DisplayName("Integration > update only appointment title")
    void test6() {
        Appointment existingAppointment = appointmentRepository.findAll().getFirst();

        AppointmentDTO appointmentDTO = new AppointmentDTO(
            existingAppointment.getId(),
            "Only T Updated Appointment",
            null,
            null,
            null,
            null
        );

        Appointment updatedAppointment = appointmentService.updateAppointment(appointmentDTO);

        assertEquals(updatedAppointment.getId(), existingAppointment.getId());
        assertEquals(updatedAppointment.getDoctor().getId(), existingAppointment.getDoctor().getId());
        assertEquals(updatedAppointment.getPatient().getId(), existingAppointment.getPatient().getId());
        assertEquals(updatedAppointment.getAppointmentType(), existingAppointment.getAppointmentType());
        assertEquals(updatedAppointment.getStart(), existingAppointment.getStart());

        assertEquals(updatedAppointment.getTitle(), appointmentDTO.getTitle());
        assertNotEquals(updatedAppointment.getTitle(), existingAppointment.getTitle());
    }

    @Test
    @DisplayName("Integration > update the appointment start time")
    void test7() {
        Appointment existingAppointment = appointmentRepository.findAll().getFirst();

        AppointmentDTO appointmentDTO = new AppointmentDTO(
            existingAppointment.getId(),
            null,
            null,
            null,
            existingAppointment.getStart().plusMinutes(30),
            null
        );

        Appointment updatedAppointment = appointmentService.updateAppointment(appointmentDTO);

        assertEquals(updatedAppointment.getId(), existingAppointment.getId());
        assertEquals(updatedAppointment.getDoctor().getId(), existingAppointment.getDoctor().getId());
        assertEquals(updatedAppointment.getPatient().getId(), existingAppointment.getPatient().getId());
        assertEquals(updatedAppointment.getAppointmentType(), existingAppointment.getAppointmentType());
        assertEquals(updatedAppointment.getTitle(), existingAppointment.getTitle());

        assertNotEquals(updatedAppointment.getStart(), existingAppointment.getStart());
        assertNotEquals(updatedAppointment.getEnd(), existingAppointment.getEnd());

        assertEquals(updatedAppointment.getStart(), existingAppointment.getStart().plusMinutes(30));
        assertEquals(updatedAppointment.getEnd(), existingAppointment.getEnd().plusMinutes(30));
    }

    @Test
    @DisplayName("Integration > update the appointment type")
    void test8() {
        Appointment existingAppointment = appointmentRepository.findAll().getFirst();

        int nextOrdinal = (existingAppointment.getAppointmentType().ordinal() + 1) % AppointmentType.values().length;
        AppointmentType newType = AppointmentType.values()[nextOrdinal];

        AppointmentDTO appointmentDTO = new AppointmentDTO(
            existingAppointment.getId(),
            null,
            null,
            null,
            null,
            newType
        );

        Appointment updatedAppointment = appointmentService.updateAppointment(appointmentDTO);

        assertEquals(updatedAppointment.getId(), existingAppointment.getId());
        assertEquals(updatedAppointment.getDoctor().getId(), existingAppointment.getDoctor().getId());
        assertEquals(updatedAppointment.getPatient().getId(), existingAppointment.getPatient().getId());
        assertEquals(updatedAppointment.getStart(), existingAppointment.getStart());
        assertEquals(updatedAppointment.getTitle(), existingAppointment.getTitle());

        assertNotEquals(updatedAppointment.getAppointmentType(), existingAppointment.getAppointmentType());
        assertEquals(updatedAppointment.getAppointmentType(), newType);
        assertEquals(updatedAppointment.getEnd(), updatedAppointment.getStart().plusMinutes(newType.getDuration()));
    }

    @Test
    @DisplayName("Integration > update the appointment doctor")
    void test9() {
        Appointment existingAppointment = appointmentRepository.findAll().getFirst();
        User newDoctor;

        Optional<User> newDoctorOptional = doctors.stream()
            .filter(doctor -> !Objects.equals(doctor.getId(), existingAppointment.getDoctor().getId()))
            .filter(doctor -> getFreeSlots(doctor.getId(), existingAppointment.getStart().toLocalDate(), existingAppointment.getAppointmentType(), false).contains(existingAppointment.getStart()))
            .findFirst();
        assertTrue(newDoctorOptional.isPresent());

        newDoctor = newDoctorOptional.get();

        AppointmentDTO appointmentDTO = new AppointmentDTO(
            existingAppointment.getId(),
            null,
            null,
            newDoctor.getId(),
            null,
            null
        );

        Appointment updatedAppointment = appointmentService.updateAppointment(appointmentDTO);

        assertEquals(updatedAppointment.getId(), existingAppointment.getId());
        assertEquals(updatedAppointment.getPatient().getId(), existingAppointment.getPatient().getId());
        assertEquals(updatedAppointment.getAppointmentType(), existingAppointment.getAppointmentType());
        assertEquals(updatedAppointment.getStart(), existingAppointment.getStart());
        assertEquals(updatedAppointment.getTitle(), existingAppointment.getTitle());

        assertNotEquals(updatedAppointment.getDoctor().getId(), existingAppointment.getDoctor().getId());
        assertEquals(updatedAppointment.getDoctor().getId(), newDoctor.getId());
        assertEquals(updatedAppointment.getDoctor().getName(), newDoctor.getName());
        assertEquals(updatedAppointment.getDoctor().getEmail(), newDoctor.getEmail());
    }

    @Test
    @DisplayName("Integration > get available appointments for the current week (Wednesday) (should return all slots for Wednesday, Thursday, and Friday)")
    void test10() {
        LocalDate requestDate = LocalDate.now(clock).with(DayOfWeek.WEDNESDAY);

        List<LocalDateTime> availableSlots = appointmentService.getAvailableAppointments(doctors.getFirst().getId(), requestDate, AppointmentType.QUICKCHECK, true);

        assertFalse(availableSlots.isEmpty());
        assertTrue(availableSlots.stream().allMatch(slot -> slot.toLocalDate().getDayOfWeek().getValue() <= 5 && slot.toLocalDate().getDayOfWeek().getValue() >= 3),
                   "All slots should be within the work week (Monday to Friday)");
        assertTrue(availableSlots.stream().allMatch(slot -> slot.getHour() >= 8 && slot.getHour() < 17),
                   "All slots should be within work hours (8 AM to 5 PM)");
    }

    @Test
    @DisplayName("Integration > try to schedule an appointment with a doctor that is not free")
    void test11() {
        LocalDate requestDate = LocalDate.now(clock).with(DayOfWeek.WEDNESDAY);
        Appointment existingAppointment;
        Appointment anotherAppointment;
        List<Appointment> pol = appointmentRepository.findByDoctorIdAndStartBetween(
                doctors.getFirst().getId(),
                requestDate.atTime(8, 30),
                requestDate.atTime(16, 30)
        );

        existingAppointment = pol.getFirst();
        anotherAppointment = pol.getLast();

        NewAppointmentDTO newAppointmentDTO = new NewAppointmentDTO(
                existingAppointment.getTitle(),
                existingAppointment.getDoctor().getId(),
                existingAppointment.getStart().minusMinutes(15),
                existingAppointment.getAppointmentType(),
                new PatientDTO(
                    existingAppointment.getPatient().getId(),
                    existingAppointment.getPatient().getName(),
                    existingAppointment.getPatient().getBirthdate(),
                    existingAppointment.getPatient().getInsuranceNumber(),
                    existingAppointment.getPatient().getInsuranceProvider(),
                    existingAppointment.getPatient().getEmail()
                )
        );

        AppointmentDTO appointmentDTO = new AppointmentDTO(
                existingAppointment.getId(),
                existingAppointment.getTitle(),
                existingAppointment.getPatient().getId(),
                existingAppointment.getDoctor().getId(),
                anotherAppointment.getStart().minusMinutes(15),
                existingAppointment.getAppointmentType()
        );

        IllegalArgumentException eCreate = assertThrows(IllegalArgumentException.class, () -> appointmentService.scheduleAppointment(newAppointmentDTO));
        IllegalArgumentException eUpdate = assertThrows(IllegalArgumentException.class, () -> appointmentService.updateAppointment(appointmentDTO));
        assertEquals("Doctor is not free at the requested time", eCreate.getMessage());
        assertEquals("Doctor is not free at the requested time", eUpdate.getMessage());

    }
}
