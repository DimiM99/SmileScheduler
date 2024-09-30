package de.vd40xu.smilebase.dto;

import de.vd40xu.smilebase.model.emuns.AppointmentType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AppointmentDTO {
    private Long id;
    private String title;
    private Long patientId;
    private Long doctorId;
    private LocalDateTime start;
    private AppointmentType appointmentType;
    private String reasonForAppointment;
    private String notes;
}
