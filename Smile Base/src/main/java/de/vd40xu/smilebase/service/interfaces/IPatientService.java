package de.vd40xu.smilebase.service.interfaces;

import de.vd40xu.smilebase.dto.PatientDTO;
import de.vd40xu.smilebase.model.Patient;

import java.util.Optional;

public interface IPatientService {
    Optional<Patient> getPatientById(long id);
    Optional<Patient> getPatientByInsuranceNumber(String insuranceNumber);
    Optional<Patient> getPatientByEmail(String email);
    Patient savePatient(PatientDTO patient);
    void deletePatient(Long id);
    Patient updatePatient(PatientDTO patient);
}
