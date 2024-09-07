package de.vd40xu.smilebase.dto;

import de.vd40xu.smilebase.model.emuns.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String name;
    private String email;
    private UserRole role;
}
