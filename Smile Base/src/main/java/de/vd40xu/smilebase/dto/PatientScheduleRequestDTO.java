package de.vd40xu.smilebase.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PatientScheduleRequestDTO {
    private String receivedHash;
    private Long patientId;
    private LocalDate patientDateOfBirth;
}
