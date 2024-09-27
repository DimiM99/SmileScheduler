package de.vd40xu.smilebase.service.unit;

import de.vd40xu.smilebase.dto.PatientDTO;
import de.vd40xu.smilebase.model.Patient;
import de.vd40xu.smilebase.repository.PatientRepository;
import de.vd40xu.smilebase.service.PatientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock private PatientRepository patientRepository;

    @InjectMocks private PatientService patientService;

    @Test
    @DisplayName("Unit > get patient by id")
    void test1() {
        Patient patient = new Patient(
                1L,
                "Max Mustermann",
                LocalDate.of(1990, 1, 1),
                "123456789",
                "Provider 1",
                "email@tes.de",
                "+49 911 3456 7890",
                new HashSet<>()
        );
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        Optional<Patient> result = patientService.getPatientById(1L);

        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Unit > get patient by insurance number")
    void test2() {
        Patient patient = new Patient(
                1L,
                "Max Mustermann",
                LocalDate.of(1990, 1, 1),
                "123456789",
                "Provider 1",
                "email@tets.de",
                "+49 911 3456 7890",
                new HashSet<>()
        );
        when(patientRepository.findByInsuranceNumber("123456789")).thenReturn(Optional.of(patient));

        Optional<Patient> result = patientService.getPatientByInsuranceNumber("123456789");

        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Unit > get patient by email")
    void test3() {
        Patient patient = new Patient(
                1L,
                "Max Mustermann",
                LocalDate.of(1990, 1, 1),
                "123456789",
                "Provider 1",
                "email@tets.de",
                "+49 911 3456 7890",
                new HashSet<>()
        );
        when(patientRepository.findByEmail("email@test.de")).thenReturn(Optional.of(patient));

        Optional<Patient> result = patientService.getPatientByEmail("email@test.de");

        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Unit > save patient")
    void test4() {
        PatientDTO patientDTO = new PatientDTO(
                "Max Mustermann",
                "123456789",
                LocalDate.of(1990, 1, 1),
                "Provider 1",
                "email@tets.de",
                "+49 911 3456 7890");

        Patient patient = new Patient(
                1L,
                "Max Mustermann",
                LocalDate.of(1990, 1, 1),
                "123456789",
                "Provider 1",
                "email@tets.de",
                "+49 911 3456 7890",
                new HashSet<>()
        );

        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient result = patientService.savePatient(patientDTO);

        assertEquals(result.getId(), patient.getId());
    }

    @Test
    @DisplayName("Unit > delete patient")
    void test5() {
        Long patientId = 1L;

        doNothing().when(patientRepository).deleteById(patientId);

        patientService.deletePatient(patientId);

        assertDoesNotThrow(() -> patientService.deletePatient(patientId));
    }

    @ParameterizedTest
    @DisplayName("Unit > update patient")
    @MethodSource("updatePatientTestArgsSource")
    void test6(PatientDTO patientDTO) {

        Patient patient = new Patient(
                1L,
                "Max Mustermann",
                LocalDate.of(1990, 1, 1),
                "123456789",
                "Provider 1",
                "email@tets.de",
                "+49 911 3456 7890",
                new HashSet<>()
        );
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(new Patient());

        patientService.updatePatient(patientDTO);

        verify(patientRepository, times(1)).findById(any(Long.class));
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    public static Stream<Arguments> updatePatientTestArgsSource() {
        return Stream.of(
                Arguments.of(
                        Named.of("> has new insurance number",
                                new PatientDTO(
                                        1L,
                                        "Max Mustermann",
                                        LocalDate.of(1990, 1, 1),
                                        "123456790",
                                        null,
                                        null,
                                        "+49 911 3456 7890")
                        )
                ),
                Arguments.of(
                        Named.of("> has new insurance provider",
                                new PatientDTO(
                                        1L,
                                        "Max Mustermann",
                                        LocalDate.of(1990, 1, 1),
                                        null,
                                        "provider 2",
                                        null,
                                        "+49 911 3456 7890")
                        )
                ),
                Arguments.of(
                        Named.of("> has new email",
                                new PatientDTO(
                                        1L,
                                        "Max Mustermann",
                                        LocalDate.of(1990, 1, 1),
                                        null,
                                        null,
                                        "newEmail@test.de",
                                        "+49 911 3456 7890")
                        )
                ),
                Arguments.of(
                        Named.of("> updated email and insurance",
                                new PatientDTO(
                                        1L,
                                        "Max Mustermann",
                                        LocalDate.of(1990, 1, 1),
                                        "1234123",
                                        "Provider 3",
                                        "newEmail@test.de",
                                        "+49 911 3456 7890")
                        )
                )
        );
    }
}