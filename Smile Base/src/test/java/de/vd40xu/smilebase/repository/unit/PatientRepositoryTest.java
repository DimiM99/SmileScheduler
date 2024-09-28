package de.vd40xu.smilebase.repository.unit;

import de.vd40xu.smilebase.model.Patient;
import de.vd40xu.smilebase.repository.PatientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientRepositoryTest {

    @Mock private PatientRepository patientRepository;

    @Test
    @DisplayName("Unit > should find patient by insurance number")
    void test1() {
        String insuranceNumber = "INS123456";
        Patient expectedPatient = new Patient();
        expectedPatient.setInsuranceNumber(insuranceNumber);
        when(patientRepository.findByInsuranceNumber(insuranceNumber)).thenReturn(List.of(expectedPatient));

        List<Patient> result = patientRepository.findByInsuranceNumber(insuranceNumber);

        assertFalse(result.isEmpty());
        assertEquals(insuranceNumber, result.getFirst().getInsuranceNumber());
        verify(patientRepository, times(1)).findByInsuranceNumber(insuranceNumber);
    }

    @Test
    @DisplayName("Unit > should find patient by email")
    void test2() {
        String email = "patient@example.com";
        Patient expectedPatient = new Patient();
        expectedPatient.setEmail(email);
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(expectedPatient));

        Optional<Patient> result = patientRepository.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        verify(patientRepository, times(1)).findByEmail(email);
    }
}
