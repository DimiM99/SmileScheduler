package de.vd40xu.smilebase.service;

import de.vd40xu.smilebase.dto.PatientDTO;
import de.vd40xu.smilebase.model.Patient;
import de.vd40xu.smilebase.repository.PatientRepository;
import de.vd40xu.smilebase.service.interfaces.IPatientService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PatientService implements IPatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public Optional<Patient> getPatientById(long id) {
        return patientRepository.findById(id);
    }

    @Override
    public Optional<Patient> getPatientByInsuranceNumber(String insuranceNumber) {
        return patientRepository.findByInsuranceNumber(insuranceNumber);
    }

    @Override
    public Optional<Patient> getPatientByEmail(String email) {
        return patientRepository.findByEmail(email);
    }

    @Override
    public Patient savePatient(PatientDTO patientDTO) {
        Patient patient = new Patient(
                patientDTO.getName(),
                patientDTO.getBirthdate(),
                patientDTO.getInsuranceNumber(),
                patientDTO.getInsuranceProvider(),
                patientDTO.getEmail(),
                patientDTO.getPhoneNumber()
        );
        return patientRepository.save(patient);
    }

    @Override
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }

    @Override
    public Patient updatePatient(PatientDTO patientDTO) {
        Patient savedPatient = patientRepository.findById(patientDTO.getId()).orElseThrow();
        if (patientDTO.getInsuranceNumber() != null) {
            savedPatient.setInsuranceNumber(patientDTO.getInsuranceNumber());
        }
        if (patientDTO.getInsuranceProvider() != null) {
            savedPatient.setInsuranceProvider(patientDTO.getInsuranceProvider());
        }
        if (patientDTO.getEmail() != null) {
            savedPatient.setEmail(patientDTO.getEmail());
        }
        if (patientDTO.getPhoneNumber() != null) {
            savedPatient.setPhoneNumber(patientDTO.getPhoneNumber());
        }
        return patientRepository.save(savedPatient);
    }
}
