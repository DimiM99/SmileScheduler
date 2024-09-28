package de.vd40xu.smilebase.service.integration;

import de.vd40xu.smilebase.dto.PatientDTO;
import de.vd40xu.smilebase.model.Patient;
import de.vd40xu.smilebase.repository.PatientRepository;
import de.vd40xu.smilebase.service.PatientService;
import de.vd40xu.smilebase.service.config.AuthContextConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PatientServiceTest extends AuthContextConfiguration {

    @Autowired private PatientRepository patientRepository;
    @Autowired private PatientService patientService;

    PatientDTO patientDTO = new PatientDTO(
                "Max Mustermann",
                "123456789",
                LocalDate.of(1990, 1, 1),
                "provider",
                "test@mail.de",
                "+49 911 3456 7890"
        );

    @BeforeAll
    public void setup() {
        patientRepository.deleteAll();
    }

    @Test
    @DisplayName("Integration > test saving a patient")
    void test1() {

        Patient savedPatient = patientService.savePatient(patientDTO);

        assertNotNull(savedPatient);
        assertEquals(patientDTO.getName(), savedPatient.getName());
        assertEquals(patientDTO.getInsuranceNumber(), savedPatient.getInsuranceNumber());
        assertEquals(patientDTO.getBirthdate(), savedPatient.getBirthdate());
        assertEquals(patientDTO.getInsuranceProvider(), savedPatient.getInsuranceProvider());
        assertEquals(patientDTO.getEmail(), savedPatient.getEmail());

        patientDTO.setId(savedPatient.getId());
    }

    @Test
    @DisplayName("Integration > test finding a patient by id")
    void test2() {
        Patient foundPatient = patientService.getPatientById(patientDTO.getId()).orElseThrow();

        assertNotNull(foundPatient);
        assertEquals(patientDTO.getName(), foundPatient.getName());
        assertEquals(patientDTO.getInsuranceNumber(), foundPatient.getInsuranceNumber());
        assertEquals(patientDTO.getBirthdate(), foundPatient.getBirthdate());
        assertEquals(patientDTO.getInsuranceProvider(), foundPatient.getInsuranceProvider());
        assertEquals(patientDTO.getEmail(), foundPatient.getEmail());
    }

    @Test
    @DisplayName("Integration > test finding a patient by insurance number")
    void test3() {
        Patient foundPatient = patientService.getPatientByInsuranceNumber(patientDTO.getInsuranceNumber()).orElseThrow();

        assertNotNull(foundPatient);
        assertEquals(patientDTO.getName(), foundPatient.getName());
        assertEquals(patientDTO.getInsuranceNumber(), foundPatient.getInsuranceNumber());
        assertEquals(patientDTO.getBirthdate(), foundPatient.getBirthdate());
        assertEquals(patientDTO.getInsuranceProvider(), foundPatient.getInsuranceProvider());
        assertEquals(patientDTO.getEmail(), foundPatient.getEmail());
    }

    @Test
    @DisplayName("Integration > test finding a patient by email")
    void test4() {
        Patient foundPatient = patientService.getPatientByEmail(patientDTO.getEmail()).orElseThrow();

        assertNotNull(foundPatient);
        assertEquals(patientDTO.getName(), foundPatient.getName());
        assertEquals(patientDTO.getInsuranceNumber(), foundPatient.getInsuranceNumber());
        assertEquals(patientDTO.getBirthdate(), foundPatient.getBirthdate());
        assertEquals(patientDTO.getInsuranceProvider(), foundPatient.getInsuranceProvider());
        assertEquals(patientDTO.getEmail(), foundPatient.getEmail());
    }

    @Test
    @DisplayName("Integration > test deleting a patient by id")
    void test5() {
        patientService.deletePatient(patientDTO.getId());
        assertEquals(0, patientRepository.count());
    }
}