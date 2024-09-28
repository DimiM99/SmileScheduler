package de.vd40xu.smilebase.service;

import de.vd40xu.smilebase.dto.PatientScheduleRequestDTO;
import de.vd40xu.smilebase.model.Appointment;
import de.vd40xu.smilebase.model.Patient;
import de.vd40xu.smilebase.repository.AppointmentRepository;
import de.vd40xu.smilebase.repository.PatientRepository;
import de.vd40xu.smilebase.service.interfaces.IPatientScheduleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static de.vd40xu.smilebase.service.utility.PSUtility.validateToken;

@Service
public class PatientScheduleService implements IPatientScheduleService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    public PatientScheduleService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    public List<Appointment> getPatientSchedule(PatientScheduleRequestDTO requestDTO) {
        if (validateToken(requestDTO.getReceivedHash())) {
            Optional<Patient> patient = patientRepository.findById(requestDTO.getPatientId());
            if (patient.isPresent()) {
                if (!patient.get().getBirthdate().equals(requestDTO.getPatientDateOfBirth())) {
                    throw new IllegalArgumentException("Patient date of birth does not match");
                }
                return appointmentRepository.findByPatientId(patient.get().getId()).stream().filter(
                        appointment -> appointment.getStart().isAfter(LocalDateTime.now())
                ).toList();
            } else {
                throw new IllegalArgumentException("Patient not found");
            }
        } else {
            throw new IllegalArgumentException("Invalid token");
        }
    }
}
