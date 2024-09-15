package de.vd40xu.smilebase.repository;

import de.vd40xu.smilebase.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByInsuranceNumberIs(String name);
}
