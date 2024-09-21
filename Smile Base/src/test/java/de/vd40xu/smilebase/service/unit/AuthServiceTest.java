package de.vd40xu.smilebase.service.unit;

import de.vd40xu.smilebase.dto.UserDTO;
import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.model.emuns.UserRole;
import de.vd40xu.smilebase.repository.UserRepository;
import de.vd40xu.smilebase.service.AuthService;
import de.vd40xu.smilebase.service.config.AuthContextConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class AuthServiceTest extends AuthContextConfiguration {

    @MockBean private UserRepository userRepository;
    @MockBean private PasswordEncoder passwordEncoder;

    @MockBean private AuthenticationManager authenticationManager;
    @Autowired private AuthService authService;

    private UserDTO testUserDTO;
    private User testUser;

    @BeforeEach
    public void setUp() {
        super.setUp();
        testUserDTO = new UserDTO(1L, "testUser", "testPassword", "testName", "testEmail", UserRole.RECEPTIONIST);
        when(passwordEncoder.encode("testPassword")).thenReturn("encodedPassword");
        testUser = User.builder()
                .id(testUserDTO.getId())
                .username(testUserDTO.getUsername())
                .password(passwordEncoder.encode(testUserDTO.getPassword()))
                .name(testUserDTO.getName())
                .email(testUserDTO.getEmail())
                .role(testUserDTO.getRole())
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Unit > login a user")
    public void test1() {
        when(userRepository.findByUsername(testUserDTO.getUsername())).thenReturn(Optional.of(testUser));
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        testUserDTO.getUsername(),
                        testUserDTO.getPassword()
                )
            )
        ).thenReturn(null);
        assertDoesNotThrow(() -> authService.loginUser(testUserDTO));
    }

    @Test
    @DisplayName("Unit > login a user with an inactive account")
    public void test2() {
        testUser.setActive(false);
        when(userRepository.findByUsername(testUserDTO.getUsername())).thenReturn(Optional.of(testUser));
        assertThrows(IllegalAccessException.class, () -> authService.loginUser(testUserDTO));
    }

    @Test
    @DisplayName("Unit > login a user with an unknown username")
    public void test4() {
        when(userRepository.findByUsername(testUserDTO.getUsername())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> authService.loginUser(testUserDTO));
    }

    @Test
    @DisplayName("Unit > login a user with an incorrect password")
    public void test5() {
        testUserDTO.setPassword("wrongPassword");
        when(userRepository.findByUsername(testUserDTO.getUsername())).thenReturn(Optional.of(testUser));
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        testUserDTO.getUsername(),
                        testUserDTO.getPassword()
                )
            )
        ).thenThrow(new BadCredentialsException("Incorrect password"));
        assertThrows(AuthenticationException.class, () -> authService.loginUser(testUserDTO));
    }
}