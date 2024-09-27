package de.vd40xu.smilebase.service.integration;

import static org.junit.jupiter.api.Assertions.*;

import de.vd40xu.smilebase.dto.AppointmentDTO;
import de.vd40xu.smilebase.dto.NewAppointmentDTO;
import de.vd40xu.smilebase.dto.PatientDTO;
import de.vd40xu.smilebase.model.Appointment;
import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.model.emuns.AppointmentType;
import de.vd40xu.smilebase.model.emuns.UserRole;
import de.vd40xu.smilebase.repository.AppointmentRepository;
import de.vd40xu.smilebase.repository.UserRepository;
import de.vd40xu.smilebase.service.AppointmentService;
import de.vd40xu.smilebase.service.config.AuthContextConfiguration;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AppointmentServiceTest extends AuthContextConfiguration {

    @Autowired private AppointmentService appointmentService;

    @Autowired private UserRepository userRepository;

    @Autowired private AppointmentRepository appointmentRepository;

    private List<User> doctors;
    private final Clock clock = Clock.fixed(
        LocalDate.of(2024, 1, 8).plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
        ZoneId.systemDefault()
    );

    @BeforeAll
    public void setUpTestEnv() {
        super.setUp();
        appointmentService.setClock(clock);
    }

    private List<LocalDateTime> getFreeSlots(Long docId, LocalDate date, AppointmentType appointmentType, boolean weekView) {
        return appointmentService.getAvailableAppointments(docId, date, appointmentType, weekView);
    }

    @Test
    @Order(1)
    @Sql("/test-data.sql")
    @DisplayName("Integration > Test Environment Setup")
    void test0() {
        assertDoesNotThrow(() -> {});
        doctors = userRepository.findAll().stream().filter(
            user -> user.getRole().equals(UserRole.DOCTOR)
        ).toList();
        assertEquals(3, doctors.size());
    }

    @Test
    @Order(2)
    @DisplayName("Integration > Get Available Appointments")
    void test1() {
        LocalDate date = LocalDate.now(clock);
        AppointmentType appointmentType = AppointmentType.QUICKCHECK;

        List<LocalDateTime> availableSlots = getFreeSlots(doctors.getFirst().getId(), date, appointmentType, false);

        assertFalse(availableSlots.isEmpty());
        assertTrue(availableSlots.stream().allMatch(slot -> slot.toLocalDate().equals(date)));
        assertTrue(availableSlots.stream().allMatch(slot -> slot.getHour() >= 8 && slot.getHour() < 17));
    }

    @Test
    @Order(3)
    @DisplayName("Integration > Get Available Appointments (Week View)")
    void test2() {
        LocalDate date = LocalDate.now(clock).with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        AppointmentType appointmentType = AppointmentType.QUICKCHECK;

        List<LocalDateTime> availableSlots = getFreeSlots(doctors.getFirst().getId(), date, appointmentType, true);

        assertFalse(availableSlots.isEmpty());
        assertTrue(availableSlots.stream().allMatch(slot -> slot.toLocalDate().getDayOfWeek().getValue() <= 5));
        assertTrue(availableSlots.stream().allMatch(slot -> slot.getHour() >= 8 && slot.getHour() < 17),
                   "All slots should be within work hours (8 AM to 5 PM)");
    }


    @Test
    @Order(4)
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
            "newpatient@example.com",
            "+49 911 3456 7890"
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
    @Order(5)
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
    @Order(6)
    @DisplayName("Integration > Delete Appointment")
    void test5() {
        Appointment appointmentToDelete = appointmentRepository.findAll().getFirst();
        Long appointmentId = appointmentToDelete.getId();

        appointmentService.deleteAppointment(appointmentId);

        assertThrows(Exception.class, () -> appointmentService.getAppointmentById(appointmentId));
    }

    @Test
    @Order(7)
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
    @Order(8)
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
    @Order(9)
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
    @Order(10)
    @DisplayName("Integration > update the appointment doctor")
    void test9() {
        User doc1 = userRepository.findByUsername("johnson.m").orElseThrow();
        User newDoctor = userRepository.findByUsername("smith.j").orElseThrow();

        Appointment existingAppointment = appointmentRepository.findByDoctorIdAndStartBetween(
            doc1.getId(),
            LocalDate.now(clock).atTime(12, 30),
            LocalDate.now(clock).atTime(15, 30)
        ).getFirst();

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
    @Order(11)
    @DisplayName("Integration > get available appointments for the current week (Tuesday) (should return all slots for Tuesday, Wednesday, Thursday, and Friday)")
    void test10() {
        LocalDate requestDate = LocalDate.now(clock).with(DayOfWeek.TUESDAY);

        List<LocalDateTime> availableSlots = appointmentService.getAvailableAppointments(doctors.getFirst().getId(), requestDate, AppointmentType.QUICKCHECK, true);

        assertFalse(availableSlots.isEmpty());
        assertTrue(availableSlots.stream().allMatch(slot -> slot.toLocalDate().getDayOfWeek().getValue() <= 5 && slot.toLocalDate().getDayOfWeek().getValue() >= 2),
                   "All slots should be within the work week (Monday to Friday)");
        assertTrue(availableSlots.stream().allMatch(slot -> slot.getHour() >= 8 && slot.getHour() < 17),
                   "All slots should be within work hours (8 AM to 5 PM)");
    }

    @Test
    @Order(12)
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
                    existingAppointment.getPatient().getEmail(),
                    existingAppointment.getPatient().getPhoneNumber()
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

    @Test
    @Order(13)
    @DisplayName("Integration > get booked appointments for a doctor")
    void test12() {
        User doctor = doctors.getFirst();
        LocalDate requestDate = LocalDate.now(clock).with(DayOfWeek.WEDNESDAY);
        List<Appointment> bookedAppointments = appointmentService.getAppointmentsForDoctor(doctor.getId(), requestDate, false);

        assertFalse(bookedAppointments.isEmpty());
        assertTrue(bookedAppointments.stream().allMatch(appointment -> appointment.getDoctor().getId().equals(doctor.getId())));
        assertTrue(bookedAppointments.stream().allMatch(appointment -> appointment.getStart().toLocalDate().equals(requestDate)));
    }

    @Test
    @Order(14)
    @DisplayName("Integration > get booked appointments for a doctor (week view - full week)")
    void test13() {
        User doctor = doctors.getFirst();
        LocalDate requestDate = LocalDate.now(clock).with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        List<Appointment> bookedAppointments = appointmentService.getAppointmentsForDoctor(doctor.getId(), requestDate, true);
        assertFalse(bookedAppointments.isEmpty());
        assertTrue(bookedAppointments.stream().allMatch(appointment -> appointment.getDoctor().getId().equals(doctor.getId())));
        assertTrue(bookedAppointments.stream().allMatch(appointment -> appointment.getStart().toLocalDate().getDayOfWeek().getValue() <= 5));
        assertTrue(bookedAppointments.stream().allMatch(appointment -> appointment.getStart().toLocalDate().getDayOfWeek().getValue() >= 1));
    }

    @Test
    @Order(15)
    @DisplayName("Integration > get booked appointments for a doctor (week view - partial week)")
    void test14() {
        User doctor = doctors.getFirst();
        LocalDate requestDate = LocalDate.now(clock);
        List<Appointment> bookedAppointments = appointmentService.getAppointmentsForDoctor(doctor.getId(), requestDate, true);
        assertFalse(bookedAppointments.isEmpty());
        assertTrue(bookedAppointments.stream().allMatch(appointment -> appointment.getDoctor().getId().equals(doctor.getId())));
        assertTrue(bookedAppointments.stream().allMatch(appointment -> appointment.getStart().toLocalDate().getDayOfWeek().getValue() <= 5));
        assertTrue(bookedAppointments.stream().allMatch(appointment -> appointment.getStart().toLocalDate().getDayOfWeek().getValue() >= 2));
    }
}
