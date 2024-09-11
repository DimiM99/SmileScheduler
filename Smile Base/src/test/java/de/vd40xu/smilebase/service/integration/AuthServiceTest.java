package de.vd40xu.smilebase.service.integration;

import de.vd40xu.smilebase.dto.UserDTO;
import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.model.emuns.UserRole;
import de.vd40xu.smilebase.repository.UserRepository;
import de.vd40xu.smilebase.service.AuthService;
import de.vd40xu.smilebase.service.UserService;
import de.vd40xu.smilebase.service.config.AuthContextConfiguration;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthServiceTest extends AuthContextConfiguration {

    @MockBean private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;

    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;
    private AuthService authService;

    private UserDTO testUserDTO;
    private User testUser;

    @BeforeAll
    public void setUp() {
        authService = new AuthService(authenticationManager, userService);
    }

    @BeforeEach
    public void init() {
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
        userRepository.save(testUser);
    }

    @Test
    @DisplayName("Integration > Login User")
    public void test1() {
        assertDoesNotThrow(() -> authService.loginUser(testUserDTO));
    }

    @Test
    @DisplayName("Integration > try to login an inactive user")
    public void test2() {
        testUser.setActive(false);
        userRepository.save(testUser);
        assertThrows(IllegalAccessException.class, () -> authService.loginUser(testUserDTO));
    }

    @Test
    @DisplayName("Integration > try to login with non-existing user")
    public void test3() {
        userRepository.delete(testUser);
        assertThrows(UsernameNotFoundException.class, () -> authService.loginUser(testUserDTO));
    }

}