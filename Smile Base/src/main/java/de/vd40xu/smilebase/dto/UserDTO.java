package de.vd40xu.smilebase.dto;

import de.vd40xu.smilebase.model.emuns.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String email;
    private UserRole role;

    public UserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
