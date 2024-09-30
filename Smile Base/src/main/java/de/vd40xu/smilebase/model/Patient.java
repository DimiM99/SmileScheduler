package de.vd40xu.smilebase.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "patients",
        uniqueConstraints={
            @UniqueConstraint(columnNames = {"insurance_number", "insurance_provider"})
        })
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Setter
    private String name;

    @Column(nullable = false)
    private LocalDate birthdate;

    @Column(nullable = false)
    @Setter
    private String insuranceNumber;

    @Column(nullable = false)
    @Setter
    private String insuranceProvider;

    @Column(nullable = false)
    @Setter
    private String email;

    @Column(nullable = false)
    @Setter
    private String phoneNumber;

    @Column
    @Setter
    private String allergies;

    @Column
    @Setter
    private String medicalHistory;

    @JsonIgnore
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Appointment> appointments = new HashSet<>();

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
        appointment.setPatient(this);
    }

    public void removeAppointment(Appointment appointment) {
        appointments.remove(appointment);
        appointment.setPatient(null);
    }

    public Patient(
        String name,
        LocalDate birthdate,
        String insuranceNumber,
        String insuranceProvider,
        String email,
        String phoneNumber,
        String allergies,
        String medicalHistory
    ) {
        this.name = name;
        this.birthdate = birthdate;
        this.insuranceNumber = insuranceNumber;
        this.insuranceProvider = insuranceProvider;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.allergies = allergies;
        this.medicalHistory = medicalHistory;
    }

    public Patient(
        String name,
        LocalDate birthdate,
        String insuranceNumber,
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
