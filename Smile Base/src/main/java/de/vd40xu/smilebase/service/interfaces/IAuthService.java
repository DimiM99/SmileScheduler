package de.vd40xu.smilebase.service.interfaces;

import de.vd40xu.smilebase.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetails;

public interface IAuthService {
    UserDetails loginUser(UserDTO userDto) throws IllegalAccessException;
}
