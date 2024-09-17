package de.vd40xu.smilebase.dto;

import de.vd40xu.smilebase.model.emuns.AppointmentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class NewAppointmentDTO {
    private String title;
    private Long doctorId;
    private LocalDateTime start;
    private AppointmentType appointmentType;
    private PatientDTO patient;
}
