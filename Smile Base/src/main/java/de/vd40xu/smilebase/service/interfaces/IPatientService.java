package de.vd40xu.smilebase.service.interfaces;

import de.vd40xu.smilebase.model.Patient;

import java.util.Optional;

public interface IPatientService {
    Iterable<Patient> getPatientById();
    Optional<Patient> getPatientByInsuranceNumber(String insuranceNumber);
    Optional<Patient> getPatientByEmail(String email);
    Patient savePatient(Patient patient);
    void deletePatient(Long id);
    Patient updatePatient(Patient patient);
}
