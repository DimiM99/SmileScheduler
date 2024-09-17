package de.vd40xu.smilebase.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
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
