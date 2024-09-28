package de.vd40xu.smilebase.service.interfaces;

import de.vd40xu.smilebase.dto.PatientScheduleRequestDTO;
import de.vd40xu.smilebase.model.Appointment;

import java.util.List;

public interface IPatientScheduleService {
    List<Appointment> getPatientSchedule(PatientScheduleRequestDTO requestDTO);
}
