package de.vd40xu.smilebase.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PatientDTO {

    private Long id;
    private String name;
    private String insuranceNumber;
    private String insuranceProvider;
    private String email;

    PatientDTO(
            String name,
            String insuranceNumber,
            String insuranceProvider,
            String email
    ) {
        this.name = name;
        this.insuranceNumber = insuranceNumber;
        this.insuranceProvider = insuranceProvider;
        this.email = email;
    }
}
