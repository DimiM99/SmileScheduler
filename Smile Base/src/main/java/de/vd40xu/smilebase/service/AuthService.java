package de.vd40xu.smilebase.service;

import de.vd40xu.smilebase.dto.UserDTO;
import de.vd40xu.smilebase.service.interfaces.IAuthService;
import de.vd40xu.smilebase.service.interfaces.IUserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final IUserService userService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    public UserDetails loginUser(UserDTO userDto) throws IllegalAccessException {
        UserDetails user = userService.loadUserByUsername(userDto.getUsername());
        if (!user.isEnabled()) {
            throw new IllegalAccessException("User is inactive");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDto.getUsername(),
                        userDto.getPassword()
                )
        );
        return user;
    }
}
