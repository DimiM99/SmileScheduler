package de.vd40xu.smilebase.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.vd40xu.smilebase.controller.config.ControllerIntegrationTest;
import de.vd40xu.smilebase.dto.PatientDTO;
import de.vd40xu.smilebase.model.Patient;
import de.vd40xu.smilebase.repository.PatientRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(2)
class PatientControllerTest extends ControllerIntegrationTest {

    @Autowired private ObjectMapper objectMapper;

    @Autowired private PatientRepository patientRepository;

    private String authToken;

    boolean isInitDisabled() { return !patientRepository.findAll().isEmpty(); }

    @BeforeEach
    void setup() throws IllegalAccessException {
        authToken = getRequestTokenForTest(testUserDTO);
    }

    @Test
    @Order(1)
    @DisabledIf(
            value = "isInitDisabled",
            disabledReason = "Disabled data in case data already exists"
    )
    @Sql("/test-data.sql")
    @DisplayName("Integration - init")
    void test0() { assertDoesNotThrow( () -> { } ); }

    @Test
    @Order(2)
    @DisplayName("Integration - search a patient by his insurance number")
    void test1() throws Exception {
        Patient patient = patientRepository.findAll().getFirst();
        mockMvc.perform(get("/api/patients/search")
                .header("Authorization", "Bearer " + authToken)
                .param("insuranceNumber", patient.getInsuranceNumber()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(patient))));
    }

    @Test
    @Order(3)
    @DisplayName("Integration - search a patient by his insurance number (no patient found)")
    void test2() throws Exception {
        mockMvc.perform(get("/api/patients/search")
                .header("Authorization", "Bearer " + authToken)
                .param("insuranceNumber", "NOTFOUND"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @Order(4)
    @DisplayName("Integration - update a patient")
    void test3() throws Exception {
        Patient patient= patientRepository.findAll().getFirst();
        PatientDTO testPatientDTO = new PatientDTO(
                patient.getId(),
                patient.getName(),
                patient.getBirthdate(),
                patient.getInsuranceNumber(),
                patient.getInsuranceProvider(),
                patient.getEmail(),
                patient.getPhoneNumber()
        );
        mockMvc.perform(put("/api/patients")
                .header("Authorization", "Bearer " + authToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testPatientDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(patient.getName()));
    }

    @Test
    @Order(5)
    @DisplayName("Integration - update a patient (patient not found)")
    void test4() throws Exception {
        PatientDTO testPatientDTO = new PatientDTO(
                999L,
                "Test",
                null,
                "INS999",
                "Test",
                "Test",
                "Test"
        );
        mockMvc.perform(put("/api/patients")
                .header("Authorization", "Bearer " + authToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testPatientDTO)))
                .andExpect(status().isNotFound());
    }

}