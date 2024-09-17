package de.vd40xu.smilebase.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "patients")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Date birthdate;

    @Column(nullable = false)
    @Setter
    private String insuranceNumber;

    @Column(nullable = false)
    @Setter
    private String insuranceProvider;

    @Column(nullable = false)
    @Setter
    private String email;

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
        Date birthdate,
        String insuranceNumber,
        String insuranceProvider
    ) {
        this.name = name;
        this.birthdate = birthdate;
        this.insuranceNumber = insuranceNumber;
        this.insuranceProvider = insuranceProvider;
    }

}
