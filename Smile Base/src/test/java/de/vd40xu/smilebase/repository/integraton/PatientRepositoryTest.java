package de.vd40xu.smilebase.repository.integraton;

import de.vd40xu.smilebase.model.Patient;
import de.vd40xu.smilebase.repository.PatientRepository;
import de.vd40xu.smilebase.repository.config.IntegrationRepositoryTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PatientRepositoryTest extends IntegrationRepositoryTest {

    @Autowired private PatientRepository patientRepository;

    Patient testPatient;

    @BeforeAll
    void beforeAll() {
        testPatient = new Patient("John Doe", LocalDate.of(1990, 1, 1), "INS123456", "Provider A", "john@example.com", "+49 911 1234 0000");
        testPatient = patientRepository.save(testPatient);
    }

    @Test
    @DisplayName("Integration > Find Patient by Insurance Number")
    void test1() {
        List<Patient> result = patientRepository.findByInsuranceNumber(testPatient.getInsuranceNumber());

        assertFalse(result.isEmpty());
        assertEquals(testPatient.getInsuranceNumber(), result.getFirst().getInsuranceNumber());
    }

    @Test
    @DisplayName("Integration > Find Patient by Email")
    void test2() {
        Optional<Patient> result = patientRepository.findByEmail(testPatient.getEmail());

        assertTrue(result.isPresent());
        assertEquals(testPatient.getEmail(), result.get().getEmail());
    }

    @Test
    @DisplayName("Integration > Find Patient by Non-existent Insurance Number")
    void test3() {
        List<Patient> result = patientRepository.findByInsuranceNumber("NONEXISTENT");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Integration > Find Patient by Non-existent Email")
    void test4() {
        Optional<Patient> result = patientRepository.findByEmail("nonexistent@example.com");

        assertTrue(result.isEmpty());
    }
}
