package de.vd40xu.smilebase.controller.config;

import de.vd40xu.smilebase.SmileBaseApplication;
import de.vd40xu.smilebase.dto.UserDTO;
import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.model.emuns.UserRole;
import de.vd40xu.smilebase.repository.UserRepository;
import de.vd40xu.smilebase.service.AuthService;
import de.vd40xu.smilebase.service.utility.JwtService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = SmileBaseApplication.class)
@ExtendWith({MockitoExtension.class, PostgresqlTestContainerExtension.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("container")
public abstract class ControllerIntegrationTest {

    @Autowired public MockMvc mockMvc;

    @Autowired public JwtService jwtService;

    @Autowired public UserRepository userRepository;

    @Autowired public AuthService authService;

    @Autowired public PasswordEncoder passwordEncoder;

    @Autowired JdbcTemplate jdbcTemplate;

    public UserDTO testUserDTO = new UserDTO(
                1L,
                "testUser",
                "testPassword",
                "testName",
                "test@email.domain",
                UserRole.RECEPTIONIST
    );

    @BeforeAll
    public void setUp() {
        userRepository.save(
                User.builder()
                        .username(testUserDTO.getUsername())
                        .password(passwordEncoder.encode(testUserDTO.getPassword()))
                        .name(testUserDTO.getName())
                        .email(testUserDTO.getEmail())
                        .role(testUserDTO.getRole())
                        .active(true)
                        .build()
        );
    }

    public String getRequestTokenForTest(UserDTO userDTO) throws IllegalAccessException {
        UserDetails authenticatedUser = authService.loginUser(userDTO);

        return jwtService.generateToken(authenticatedUser);
    }

    @AfterAll
    public void clean() {
        jdbcTemplate.execute("DELETE FROM appointments");
        jdbcTemplate.execute("DELETE FROM users WHERE username != 'adminUser'");
    }
}
