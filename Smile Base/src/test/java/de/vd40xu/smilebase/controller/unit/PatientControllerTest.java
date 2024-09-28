package de.vd40xu.smilebase.controller.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.vd40xu.smilebase.config.security.JwtAuthenticationFilter;
import de.vd40xu.smilebase.controller.PatientController;
import de.vd40xu.smilebase.dto.PatientDTO;
import de.vd40xu.smilebase.model.Patient;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientController.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class PatientControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private PatientService patientService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
            return Mockito.mock(JwtAuthenticationFilter.class);
        }
    }

    @Test
    @DisplayName("Unit > find a patient, GET /api/patients/search")
    void test1() throws Exception {
        when(
          patientService.getPatientByInsuranceNumber("1234567890")
        ).thenReturn(Optional.of(new Patient()));
        mockMvc.perform(get("/api/patients/search?insuranceNumber=1234567890"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Unit > try finding a patient that doesn't exist, GET /api/patients/search")
    void test2() throws Exception {
        when(
          patientService.getPatientByInsuranceNumber("1234567890")
        ).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/patients/search?insuranceNumber=1234567890"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Unit > update a patient, PUT /api/patients")
    void test3() throws Exception {
        PatientDTO patientDTO = new PatientDTO(
                1L,
                "John Doe",
                LocalDate.of(1990, 1, 1),
                "12451341",
                "Provider",
                "testpatione@emial.de",
                "+49123456789"
        );
        Patient patient = new Patient(
                1L,
                "John Doe",
                LocalDate.of(1990, 1, 1),
                "12451341",
                "Provider",
                "testpatione@emial.de",
                "+49123456789",
                new HashSet<>()
        );
        when(
          patientService.updatePatient(any(PatientDTO.class))
        ).thenReturn(patient);
        mockMvc.perform(put("/api/patients")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(patient)));
    }

    @Test
    @DisplayName("Unit > try updating a patient that doesn't exist, PUT /api/patients")
    void test4() throws Exception {
        PatientDTO patientDTO = new PatientDTO(
                1L,
                "John Doe",
                LocalDate.of(1990, 1, 1),
                "12451341",
                "Provider",
                "testpatione@emial.de",
                "+49123456789"
        );
        when(
          patientService.updatePatient(any(PatientDTO.class))
        ).thenThrow(new NoSuchElementException("Patient not found"));

        mockMvc.perform(put("/api/patients")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Patient not found"));
    }
}