package de.vd40xu.smilebase.model;

import de.vd40xu.smilebase.model.emuns.AppointmentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Setter
    private String title;

    @Column(nullable = false)
    @Setter
    private LocalDateTime start;

    @Column(nullable = false)
    @Setter
    private AppointmentType appointmentType;

    @Column(name = "`end`", nullable = false)
    @Setter
    private LocalDateTime end;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    @Setter
    private User doctor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    @Setter
    private Patient patient;

    public Appointment(String title, LocalDateTime start, AppointmentType appointmentType) {
        this.title = title;
        this.start = start;
        this.appointmentType = appointmentType;
        this.end = start.plusMinutes(appointmentType.getDuration());
    }
}
