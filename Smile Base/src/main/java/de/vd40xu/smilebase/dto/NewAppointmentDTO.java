package de.vd40xu.smilebase.dto;

import de.vd40xu.smilebase.model.emuns.AppointmentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewAppointmentDTO {
    private String title;
    private Long doctorId;
    private LocalDateTime start;
    private AppointmentType appointmentType;
    private PatientDTO patient;
    private String reasonForAppointment;
    private String notes;

    public NewAppointmentDTO(
            String title,
            Long doctorId,
            LocalDateTime start,
            AppointmentType appointmentType,
            PatientDTO patient
            ) {
        this.title = title;
        this.doctorId = doctorId;
        this.start = start;
        this.appointmentType = appointmentType;
        this.patient = patient;
    }
}
