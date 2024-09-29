package de.vd40xu.smilebase.controller.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.vd40xu.smilebase.config.security.JwtAuthenticationFilter;
import de.vd40xu.smilebase.controller.PatientScheduleController;
import de.vd40xu.smilebase.dto.PatientScheduleRequestDTO;
import de.vd40xu.smilebase.service.PatientScheduleService;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(PatientScheduleController.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class PatientScheduleControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private PatientScheduleService patientScheduleService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
            return Mockito.mock(JwtAuthenticationFilter.class);
        }
    }

    @Test
    @DisplayName("Unit > test getting patient schedule")
    void test1() throws Exception {
        when(patientScheduleService.getPatientSchedule(any(PatientScheduleRequestDTO.class))).thenReturn(List.of());
        mockMvc.perform(post("/api/patient-schedule")
                .content(objectMapper.writeValueAsString(new PatientScheduleRequestDTO()))
                .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Unit > test getting patient schedule with invalid request")
    void test2() throws Exception {
        when(patientScheduleService.getPatientSchedule(any(PatientScheduleRequestDTO.class))).thenThrow(new IllegalArgumentException("Invalid request"));
        mockMvc.perform(post("/api/patient-schedule")
                .content(objectMapper.writeValueAsString(new PatientScheduleRequestDTO()))
                .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

}