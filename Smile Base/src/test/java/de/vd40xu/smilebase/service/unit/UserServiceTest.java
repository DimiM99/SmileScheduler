package de.vd40xu.smilebase.service.unit;

import de.vd40xu.smilebase.dto.UserDTO;
import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.model.emuns.UserRole;
import de.vd40xu.smilebase.repository.UserRepository;
import de.vd40xu.smilebase.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserService userService;

    private User testUser;
    private UserDetails testUserDetails;

    @BeforeEach
    public void setUp() {

        UserDTO testUserDTO = new UserDTO(
                1L,
                "testUser",
                "testPassword",
                "testName",
                "test@email.domain",
                UserRole.RECEPTIONIST
        );

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

        testUserDetails = org.springframework.security.core.userdetails.User.builder()
                          .username(testUserDTO.getUsername())
                          .password(passwordEncoder.encode(testUserDTO.getPassword()))
                          .disabled(!testUser.isActive())
                          .build();
    }

    @Test
    @DisplayName("Unit > Test loading user by username")
    void test1() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        UserDetails loadedUser = userService.loadUserByUsername("testUser");
        assertEquals(testUserDetails, loadedUser);
    }

    @Test
    @DisplayName("Unit > Test loading user by username with non-existing user")
    void test2() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("testUser"));
    }

    @Test
    @DisplayName("Unit > test loading user by principal")
    void test3() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        Optional<User> loadedUser = userService.loadUserFromPrincipal(testUserDetails);
        assertTrue(loadedUser.isPresent());
        assertEquals(testUser, loadedUser.get());
    }
}

