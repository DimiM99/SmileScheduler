package de.vd40xu.smilebase.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.vd40xu.smilebase.controller.config.ControllerIntegrationTest;
import de.vd40xu.smilebase.dto.AppointmentDTO;
import de.vd40xu.smilebase.dto.NewAppointmentDTO;
import de.vd40xu.smilebase.dto.PatientDTO;
import de.vd40xu.smilebase.model.Appointment;
import de.vd40xu.smilebase.model.Patient;
import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.model.emuns.AppointmentType;
import de.vd40xu.smilebase.repository.AppointmentRepository;
import de.vd40xu.smilebase.repository.PatientRepository;
import de.vd40xu.smilebase.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(1)
class AppointmentControllerTest extends ControllerIntegrationTest {

    @Autowired private ObjectMapper objectMapper;

    @Autowired private AppointmentRepository appointmentRepository;

    @Autowired private PatientRepository patientRepository;

    @Autowired UserRepository userRepository;

    private String authToken;

    @BeforeEach
    void setup() throws IllegalAccessException {
        authToken = getRequestTokenForTest(testUserDTO);
    }

    @Test
    @Sql({"/test-data.sql"})
    @Order(1)
    @DisplayName("Integration - init")
    void test0() { assertDoesNotThrow( () -> { } ); }

    @Test
    @Order(2)
    @DisplayName("Integration > Get Free Appointment Slots, GET /api/appointments/free-slots")
    void test1() throws Exception {
        User doctor = userRepository.findByUsername("smith.j").orElseThrow();

        mockMvc.perform(get("/api/appointments/free-slots")
                .header("Authorization", "Bearer " + authToken)
                .param("doctorId", doctor.getId().toString())
                .param("date", "2023-06-01")
                .param("appointmentType", "QUICKCHECK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(3)
    @DisplayName("Integration > Schedule New Appointment, POST /api/appointments")
    void test2() throws Exception {
        User doctor = userRepository.findByUsername("smith.j").orElseThrow();
        Patient patient = patientRepository.findByInsuranceNumber("INS001").getFirst();

        NewAppointmentDTO appointmentDTO = new NewAppointmentDTO(
                "New Appointment",
                doctor.getId(),
                LocalDateTime.now().plusDays(1).withHour(10).withMinute(0),
                AppointmentType.QUICKCHECK,
                new PatientDTO(patient.getId(), patient.getName(), patient.getBirthdate(),
                               patient.getInsuranceNumber(), patient.getInsuranceProvider(), patient.getEmail(), patient.getPhoneNumber())
        );

        mockMvc.perform(post("/api/appointments")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Appointment"));
    }

    @Test
    @Order(4)
    @DisplayName("Integration > Get Appointment Details, GET /api/appointments")
    void test3() throws Exception {
        Appointment appointment = appointmentRepository.findAll().getFirst();

        mockMvc.perform(get("/api/appointments")
                .header("Authorization", "Bearer " + authToken)
                .param("appointmentId", appointment.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(appointment.getId()));
    }

    @Test
    @Order(5)
    @DisplayName("Integration > Update Existing Appointment, PUT /api/appointments")
    void test6() throws Exception {
        Appointment appointment = appointmentRepository.findAll().getFirst();

        AppointmentDTO appointmentDTO = new AppointmentDTO(
                appointment.getId(),
                "Updated Appointment",
                null,
                null,
                null,
                appointment.getAppointmentType()
        );

        mockMvc.perform(put("/api/appointments")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Appointment"));
    }

    @Test
    @Order(6)
    @DisplayName("Integration > Cancel Appointment, DELETE /api/appointments/{appointmentId}")
    void test7() throws Exception {
        Appointment appointment = appointmentRepository.findAll().getFirst();

        mockMvc.perform(delete("/api/appointments/" + appointment.getId())
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/appointments")
                .header("Authorization", "Bearer " + authToken)
                .param("appointmentId", appointment.getId().toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(7)
    @DisplayName("Integration > Get Free Appointment Slots (without a doctor id), GET /api/appointments/free-slots")
    void test8() throws Exception {
        User notDoc = userRepository.findByUsername("max.m").orElseThrow();
        mockMvc.perform(get("/api/appointments/free-slots")
                .header("Authorization", "Bearer " + authToken)
                .param("doctorId", String.valueOf(notDoc.getId()))
                .param("date", "2023-06-01")
                .param("appointmentType", "QUICKCHECK"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The id provided is not a doctor id"));
    }

    @Test
    @Order(8)
    @DisplayName("Integration > Get Free Appointment Slots with incorrect request, GET /api/appointments/free-slots")
    void test9() throws Exception {
        User notDoc = userRepository.findByUsername("max.m").orElseThrow();
        mockMvc.perform(get("/api/appointments/free-slots")
                .header("Authorization", "Bearer " + authToken)
                .param("doctorId", String.valueOf(notDoc.getId()))
                .param("date", "2023-06")
                .param("appointmentType", "QUICKCHECK"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(9)
    @DisplayName("Integration > try scheduling an appointment outside clinic hours, POST /api/appointments")
    void test10() throws Exception {
        User doctor = userRepository.findByUsername("smith.j").orElseThrow();
        Patient patient = patientRepository.findByInsuranceNumber("INS001").getFirst();

        NewAppointmentDTO appointmentDTO = new NewAppointmentDTO(
                "New Appointment",
                doctor.getId(),
                LocalDateTime.now().plusDays(1).withHour(7).withMinute(30),
                AppointmentType.QUICKCHECK,
                new PatientDTO(patient.getId(), patient.getName(), patient.getBirthdate(),
                               patient.getInsuranceNumber(), patient.getInsuranceProvider(), patient.getEmail() , patient.getPhoneNumber())
        );

        mockMvc.perform(post("/api/appointments")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentDTO)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Appointment time is outside clinic hours"));
    }

    @Test
    @Order(10)
    @DisplayName("Integration > try getting appointment details with incorrect id, GET /api/appointments")
    void test11() throws Exception {
        mockMvc.perform(get("/api/appointments")
                .header("Authorization", "Bearer " + authToken)
                .param("appointmentId", "0"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("The appointment with the provided id does not exist"));
    }

    @Test
    @Order(11)
    @DisplayName("Integration > try updating an appointment with incorrect id, PUT /api/appointments")
    void test12() throws Exception {
        Appointment appointment = appointmentRepository.findAll().getFirst();

        AppointmentDTO appointmentDTO = new AppointmentDTO(
                0L,
                "Updated Appointment",
                appointment.getPatient().getId(),
                appointment.getDoctor().getId(),
                appointment.getStart(),
                AppointmentType.EXTENSIVE
        );

        mockMvc.perform(put("/api/appointments")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("The appointment with the provided id does not exist"));
    }

    @Test
    @Order(12)
    @DisplayName("Integration > try changing an appointments doctor where the doctor isn't available, PUT /api/appointments")
    void test13() throws Exception {
        Appointment appointment = appointmentRepository.findAll().get(3);
        User doctor = userRepository.findByUsername("williams.d").orElseThrow();

        AppointmentDTO appointmentDTO = new AppointmentDTO(
                appointment.getId(),
                "Updated Appointment",
                appointment.getPatient().getId(),
                doctor.getId(),
                appointment.getStart(),
                appointment.getAppointmentType()
        );

        mockMvc.perform(put("/api/appointments")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentDTO)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Doctor is not free at the requested time"));
    }

    @Test
    @Order(13)
    @DisplayName("Integration > get Appointments for a doctor, GET /api/appointments/booked")
    void test14() throws Exception {
        User doctor = userRepository.findByUsername("williams.d").orElseThrow();
        LocalDate requestDate = LocalDate.now().plusDays(1);
        List<Appointment> expectedAppointments = appointmentRepository.findByDoctorIdAndStartBetween(
                doctor.getId(),
                requestDate.atStartOfDay(),
                requestDate.atTime(23, 59)
        );
        mockMvc.perform(get("/api/appointments/booked")
                .header("Authorization", "Bearer " + authToken)
                .param("doctorId", doctor.getId().toString())
                .param("date", requestDate.toString())
                .param("weekView", "false"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedAppointments)));
    }

    @Test
    @Order(14)
    @DisplayName("Integration > get Appointments for a doctor that doesn't exist, GET /api/appointments/booked")
    void test15() throws Exception {
        LocalDate requestDate = LocalDate.now().plusDays(1);

        mockMvc.perform(get("/api/appointments/booked")
                .header("Authorization", "Bearer " + authToken)
                .param("doctorId", "not-a-doctor")
                .param("date", requestDate.toString())
                .param("weekView", "false"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(15)
    @DisplayName("Integration > get Appointments for a user that isn't a doctor, GET /api/appointments/booked")
    void test16() throws Exception {
        User user = userRepository.findByUsername("max.m").orElseThrow();
        LocalDate requestDate = LocalDate.now().plusDays(1);

        mockMvc.perform(get("/api/appointments/booked")
                .header("Authorization", "Bearer " + authToken)
                .param("doctorId", user.getId().toString())
                .param("date", requestDate.toString())
                .param("weekView", "false"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The id provided is not a doctor id"));
    }
}