package de.vd40xu.smilebase.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.vd40xu.smilebase.model.emuns.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Builder
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    @Setter
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Setter
    private String email;

    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    @Setter
    private boolean active;

    @JsonIgnore
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private Set<Appointment> appointments = new HashSet<>();

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
        appointment.setDoctor(this);
    }

    public void removeAppointment(Appointment appointment) {
        appointments.remove(appointment);
        appointment.setDoctor(null);
    }

    public User() { }

    public User(Long id, String username, String password, String name, String email, UserRole role, boolean active) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.role = role;
        this.active = active;
    }
}