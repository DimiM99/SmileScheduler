package de.vd40xu.smilebase.model;

import de.vd40xu.smilebase.model.emuns.UserRole;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Builder
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