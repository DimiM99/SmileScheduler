package de.vd40xu.smilebase.service;

import de.vd40xu.smilebase.dto.UserDTO;
import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.repository.UserRepository;
import de.vd40xu.smilebase.service.interfaces.IAuthService;
import de.vd40xu.smilebase.service.interfaces.IUserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final IUserService userService;

    public AuthService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            UserService userService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    public void registerUser(UserDTO userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent())
            throw new IllegalArgumentException("Username already taken. Please try again");

        userRepository.save(User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .name(userDto.getName())
                .email(userDto.getEmail())
                .role(userDto.getRole())
                .active(true)
                .build());
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
