package de.vd40xu.smilebase.service.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private static UserRepository userRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2023-06-15T10:00:00Z"), ZoneId.systemDefault());
        appointmentService.setClock(fixedClock);
    }

    @Test
    @DisplayName("Unit > Get Available Appointments")
    void test1() {
        Long doctorId = 1L;
        LocalDate date = LocalDate.now(fixedClock);
        AppointmentType appointmentType = AppointmentType.QUICKCHECK;

        User doctor = User.builder()
                .id(doctorId)
                .username("doctor1")
                .password("password")
                .name("Dr. Smith")
                .email("dr.smith@example.com")
                .role(UserRole.DOCTOR)
                .active(true)
                .build();

        when(userRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctorIdAndStartBetween(eq(doctorId), any(), any()))
            .thenReturn(new ArrayList<>());

        List<LocalDateTime> result = appointmentService.getAvailableAppointments(doctorId, date, appointmentType, false);

        assertFalse(result.isEmpty());
        assertEquals(LocalDateTime.now(fixedClock).withHour(8).withMinute(0), result.getFirst());
    }

    @Test
    @DisplayName("Unit > Schedule Appointment")
    void test2() {
        LocalDateTime appointmentStart = LocalDateTime.now(fixedClock);
        PatientDTO patientDTO = new PatientDTO(
            "John Doe",
            "INS123",
            LocalDate.of(1990, 1, 1),
            "Provider A",
            "john@example.com",
                "+49 911 3456 7890"
        );
        NewAppointmentDTO appointmentDTO = new NewAppointmentDTO(
            "Test Appointment",
            1L,
            appointmentStart,
            AppointmentType.QUICKCHECK,
            patientDTO
        );

        User doctor = User.builder()
                .id(1L)
                .username("doctor1")
                .password("password")
                .name("Dr. Smith")
                .email("dr.smith@example.com")
                .role(UserRole.DOCTOR)
                .active(true)
                .build();

        Patient patient = new Patient(
            patientDTO.getName(),
            patientDTO.getBirthdate(),
            patientDTO.getInsuranceNumber(),
            patientDTO.getInsuranceProvider(),
            patientDTO.getEmail(),
            patientDTO.getPhoneNumber()
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.save(any())).thenReturn(patient);
        when(appointmentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Appointment result = appointmentService.scheduleAppointment(appointmentDTO);

        assertNotNull(result);
        assertEquals("Test Appointment", result.getTitle());
        assertEquals(doctor, result.getDoctor());
        assertEquals(patient, result.getPatient());
    }

    @Test
    @DisplayName("Unit > Get Appointment By Id")
    void test3() {
        Long appointmentId = 1L;
        Appointment appointment = new Appointment("Test Appointment", LocalDateTime.now(fixedClock), AppointmentType.QUICKCHECK);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        Appointment result = appointmentService.getAppointmentById(appointmentId);

        assertNotNull(result);
        assertEquals("Test Appointment", result.getTitle());
    }

    @Test
    @DisplayName("Unit > Update Appointment")
    void test4() {
        LocalDateTime updatedStart = LocalDateTime.now(fixedClock);
        AppointmentDTO appointmentDTO = new AppointmentDTO(
            1L,
            "Updated Appointment",
            2L,
            2L,
            updatedStart,
            AppointmentType.EXTENSIVE,
            null,
            null
        );

        Appointment existingAppointment = new Appointment("Original Appointment", LocalDateTime.now(fixedClock).minusDays(1), AppointmentType.QUICKCHECK);

        User newDoctor = User.builder()
                .id(2L)
                .username("doctor2")
                .password("password")
                .name("Dr. Johnson")
                .email("dr.johnson@example.com")
                .role(UserRole.DOCTOR)
                .active(true)
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existingAppointment));
        when(userRepository.findById(2L)).thenReturn(Optional.of(newDoctor));
        when(appointmentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Appointment result = appointmentService.updateAppointment(appointmentDTO);

        assertNotNull(result);
        assertEquals("Updated Appointment", result.getTitle());
        assertEquals(AppointmentType.EXTENSIVE, result.getAppointmentType());
        assertEquals(newDoctor, result.getDoctor());
        assertEquals(updatedStart, result.getStart());
    }

    @Test
    @DisplayName("Unit > Delete Appointment")
    void test5() {
        Long appointmentId = 1L;

        appointmentService.deleteAppointment(appointmentId);

        verify(appointmentRepository, times(1)).deleteById(appointmentId);
    }

    @Test
    @DisplayName("Unit > Try scheduling appointment outside of working hours")
    void test6() {
        LocalDateTime appointmentStart = LocalDateTime.now(fixedClock).withHour(20);
        PatientDTO patientDTO = new PatientDTO(
            "John Doe",
            "INS123",
            LocalDate.of(1990, 1, 1),
            "Provider A",
            "john@example.com",
            "+49 911 3456 7890"
        );
        NewAppointmentDTO appointmentDTO = new NewAppointmentDTO(
            "New Test Appointment",
            1L,
            appointmentStart,
            AppointmentType.QUICKCHECK,
            patientDTO
        );
        assertThrows(IllegalArgumentException.class, () -> appointmentService.scheduleAppointment(appointmentDTO));
    }

    @Test
    @DisplayName("Unit > Try scheduling appointment with non-doctor as doctor")
    void test7() {
        LocalDateTime appointmentStart = LocalDateTime.now(fixedClock);
        User notDoc = User.builder()
                .id(2L)
                .username("receptionist")
                .password("password")
                .name("Non Doctor")
                .email("receptionist@mail.exmple")
                .role(UserRole.RECEPTIONIST)
                .build();
        PatientDTO patientDTO = new PatientDTO(
            "John Doe",
            "INS123",
            LocalDate.of(1990, 1, 1),
            "Provider A",
            "john@example.com",
            "+49 911 3456 7890"
        );
        NewAppointmentDTO appointmentDTO = new NewAppointmentDTO(
            "New Test Appointment",
            2L,
            appointmentStart,
            AppointmentType.QUICKCHECK,
            patientDTO
        );

        when(userRepository.findById(2L)).thenReturn(Optional.of(notDoc));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> appointmentService.scheduleAppointment(appointmentDTO));
        assertEquals("User is not a doctor", exception.getMessage());
    }

    @Test
    @DisplayName("Unit > Try scheduling appointment for a non-existing patient")
    void test8() {
        LocalDateTime appointmentStart = LocalDateTime.now(fixedClock);
        User doc = User.builder()
                .id(1L)
                .username("receptionist")
                .password("password")
                .name("Non Doctor")
                .email("receptionist@mail.exmple")
                    .role(UserRole.DOCTOR)
                .build();
        PatientDTO patientDTO = new PatientDTO(
            2L,
            "John Doe",
            LocalDate.of(1990, 1, 1),
            "INS123",
            "Provider A",
            "john@example.com",
            "+49 911 3456 7890",
            null,
                null
        );
        NewAppointmentDTO appointmentDTO = new NewAppointmentDTO(
            "New Test Appointment",
            1L,
            appointmentStart,
            AppointmentType.QUICKCHECK,
            patientDTO
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(doc));
        when(patientRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> appointmentService.scheduleAppointment(appointmentDTO));
        assertEquals("Patient not found", exception.getMessage());
    }

    @Test
    @DisplayName("Unit > Try creating appointment with non-existing doctor")
    void test9() {
        LocalDateTime appointmentStart = LocalDateTime.now(fixedClock);
        PatientDTO patientDTO = new PatientDTO(
            "John Doe",
            "INS123",
            LocalDate.of(1990, 1, 1),
            "Provider A",
            "john@example.com",
            "+49 911 3456 7890"
        );
        NewAppointmentDTO appointmentDTO = new NewAppointmentDTO(
            "New Test Appointment",
            1L,
            appointmentStart,
            AppointmentType.QUICKCHECK,
            patientDTO
        );

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> appointmentService.scheduleAppointment(appointmentDTO));
        assertEquals("Doctor not found", exception.getMessage());
    }

    @Test
    @DisplayName("Unit > Try creating an appointment that starts too late or too early in the day")
    void test10() {
        LocalDateTime appointmentStart = LocalDateTime.now(fixedClock);
        PatientDTO patientDTO = new PatientDTO(
            "John Doe",
            "INS123",
            LocalDate.of(1990, 1, 1),
            "Provider A",
            "john@example.com",
            "+49 911 3456 7890"
        );
        NewAppointmentDTO appointmentDTO1_too_late = new NewAppointmentDTO(
            "New Test Appointment",
            1L,
            appointmentStart.withHour(16).withMinute(30),
            AppointmentType.EXTENSIVE, // will take longer then 30 minutes left in the working day
            patientDTO
        );
        NewAppointmentDTO appointmentDTO2_too_early = new NewAppointmentDTO(
            "New Test Appointment",
            1L,
            appointmentStart.withHour(7).withMinute(30), // 30 min before opening hours
            AppointmentType.EXTENSIVE,
            patientDTO
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> appointmentService.scheduleAppointment(appointmentDTO1_too_late));
        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> appointmentService.scheduleAppointment(appointmentDTO2_too_early));
        assertEquals("Appointment time is outside clinic hours", exception.getMessage());
        assertEquals("Appointment time is outside clinic hours", exception2.getMessage());
    }

    @Test
    @DisplayName("Unit > Update an appointment with non-doctor as doctor")
    void test11() {
        User notDoc = User.builder().id(2L)
                                    .username("receptionist")
                                    .password("password")
                                    .name("Non Doctor")
                                    .email("test@mail.de")
                                    .role(UserRole.RECEPTIONIST)
                                    .build();
        AppointmentDTO appointmentDTO = new AppointmentDTO(
            1L,
            "Updated Appointment",
            2L,
            2L,
            LocalDateTime.now(),
            AppointmentType.QUICKCHECK,
            null,
            null
        );
        Appointment existingAppointment = new Appointment("Original Appointment", LocalDateTime.now(fixedClock).minusDays(1), AppointmentType.QUICKCHECK);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existingAppointment));
        when(userRepository.findById(any())).thenReturn(Optional.of(notDoc));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> appointmentService.updateAppointment(appointmentDTO));
        assertEquals("User is not a doctor", exception.getMessage());

    }

    @Test
    @DisplayName("Unit > Update an appointment with non-existing doctor")
    void test12() {
        AppointmentDTO appointmentDTO = new AppointmentDTO(
            1L,
            "Updated Appointment",
            2L,
            2L,
            LocalDateTime.now(),
            AppointmentType.QUICKCHECK,
            null,
            null
        );
        Appointment existingAppointment = new Appointment("Original Appointment", LocalDateTime.now(fixedClock).minusDays(1), AppointmentType.QUICKCHECK);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existingAppointment));
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> appointmentService.updateAppointment(appointmentDTO));
        assertEquals("Doctor not found", exception.getMessage());
    }

}

