package de.vd40xu.smilebase.controller.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.vd40xu.smilebase.config.security.JwtAuthenticationFilter;
import de.vd40xu.smilebase.controller.AppointmentController;
import de.vd40xu.smilebase.dto.AppointmentDTO;
import de.vd40xu.smilebase.dto.NewAppointmentDTO;
import de.vd40xu.smilebase.dto.PatientDTO;
import de.vd40xu.smilebase.model.Appointment;
import de.vd40xu.smilebase.model.Patient;
import de.vd40xu.smilebase.model.emuns.AppointmentType;
import de.vd40xu.smilebase.service.AppointmentService;
import de.vd40xu.smilebase.service.PatientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppointmentController.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class AppointmentControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private AppointmentService appointmentService;
    @MockBean private PatientService patientService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
            return Mockito.mock(JwtAuthenticationFilter.class);
        }
    }

    @Test
    @DisplayName("Unit > Search Patient by Insurance Number, GET /api/patients/search")
    void testSearchPatientByInsurance() throws Exception {
        Patient patient = new Patient("John Doe", LocalDate.of(1990, 1, 1), "INS123", "Provider A", "john@example.com", "+49 911 3456 0000");
        when(patientService.getPatientByInsuranceNumber("INS123")).thenReturn(Optional.of(patient));

        mockMvc.perform(get("/api/patients/search")
                .param("insuranceNumber", "INS123"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(patient)));
    }

    @Test
    @DisplayName("Unit > Search Patient by Insurance Number Not Found, GET /api/patients/search")
    void testSearchPatientByInsuranceNotFound() throws Exception {
        when(patientService.getPatientByInsuranceNumber("INS999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/patients/search")
                .param("insuranceNumber", "INS999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Unit > Get Free Appointment Slots, GET /api/appointments/free-slots")
    void testGetFreeSlots() throws Exception {
        LocalDate date = LocalDate.of(2023, 6, 1);
        LocalDateTime slot1 = LocalDateTime.of(2023, 6, 1, 9, 0);
        LocalDateTime slot2 = LocalDateTime.of(2023, 6, 1, 10, 0);
        when(appointmentService.getAvailableAppointments(1L, date, AppointmentType.QUICKCHECK, false))
                .thenReturn(Arrays.asList(slot1, slot2));

        mockMvc.perform(get("/api/appointments/free-slots")
                .param("doctorId", "1")
                .param("date", "2023-06-01")
                .param("appointmentType", "QUICKCHECK"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(slot1, slot2))));
    }

    @Test
    @DisplayName("Unit > Schedule New Appointment, POST /api/appointments")
    void testScheduleAppointment() throws Exception {
        NewAppointmentDTO appointmentDTO = new NewAppointmentDTO(
                "Check-up",
                1L,
                LocalDateTime.of(2023, 6, 1, 9, 0),
                AppointmentType.QUICKCHECK,
                new PatientDTO("John Doe", "INS123", LocalDate.of(1990, 1, 1), "Provider A", "john@example.com", "+49 911 3456 7890")
        );
        Appointment appointment = new Appointment("Check-up", LocalDateTime.of(2023, 6, 1, 9, 0), AppointmentType.QUICKCHECK);
        when(appointmentService.scheduleAppointment(any(NewAppointmentDTO.class))).thenReturn(appointment);

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(appointment)));
    }

    @Test
    @DisplayName("Unit > Get Appointment Details, GET /api/appointments")
    void testGetAppointmentDetails() throws Exception {
        Appointment appointment = new Appointment("Check-up", LocalDateTime.of(2023, 6, 1, 9, 0), AppointmentType.QUICKCHECK);
        when(appointmentService.getAppointmentById(1L)).thenReturn(appointment);

        mockMvc.perform(get("/api/appointments")
                .param("appointmentId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(appointment)));
    }

    @Test
    @DisplayName("Unit > Update Existing Appointment, PUT /api/appointments")
    void testChangeAppointment() throws Exception {
        AppointmentDTO appointmentDTO = new AppointmentDTO(
                1L,
                "Updated Check-up",
                1L,
                1L,
                LocalDateTime.of(2023, 6, 1, 10, 0),
                AppointmentType.EXTENSIVE
        );
        Appointment updatedAppointment = new Appointment("Updated Check-up", LocalDateTime.of(2023, 6, 1, 10, 0), AppointmentType.EXTENSIVE);
        when(appointmentService.updateAppointment(any(AppointmentDTO.class))).thenReturn(updatedAppointment);

        mockMvc.perform(put("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedAppointment)));
    }

    @Test
    @DisplayName("Unit > Cancel Appointment, DELETE /api/appointments/{appointmentId}")
    void testCancelAppointment() throws Exception {
        doNothing().when(appointmentService).deleteAppointment(1L);

        mockMvc.perform(delete("/api/appointments/1"))
                .andExpect(status().isOk());
    }
}
