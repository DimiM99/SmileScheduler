package de.vd40xu.smilebase.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PatientDTO {

    private Long id;
    private String name;
    private LocalDate birthdate;
    private String insuranceNumber;
    private String insuranceProvider;
    private String email;
    private String phoneNumber;

    public PatientDTO(
            String name,
            String insuranceNumber,
            LocalDate birthdate,
            String insuranceProvider,
            String email,
            String phoneNumber
    ) {
        this.name = name;
        this.birthdate = birthdate;
        this.insuranceNumber = insuranceNumber;
        this.insuranceProvider = insuranceProvider;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
