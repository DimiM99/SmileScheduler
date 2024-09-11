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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = SmileBaseApplication.class)
@ExtendWith({MockitoExtension.class, PostgresqlTestContainerExtension.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public abstract class ControllerIntegrationTest {

    @Autowired public MockMvc mockMvc;

    @Autowired public JwtService jwtService;

    @Autowired public UserRepository userRepository;

    @Autowired public AuthService authService;

    @Autowired public PasswordEncoder passwordEncoder;

    public UserDTO testUserDTO = new UserDTO(
                1L,
                "testUser",
                "testPassword",
                "testName",
                "test@email.domain",
                UserRole.RECEPTIONIST
    );

    @BeforeAll
    public void setUp() throws IllegalAccessException {
        String secret = "c4f2407efe874c8596662d70746ea48f937c1123ec5f77f0ac01ee6aea54c038";
        ReflectionTestUtils.setField(jwtService, "secretKey", secret);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 36000L);

        userRepository.save(
                User.builder()
                        .id(testUserDTO.getId())
                        .username(testUserDTO.getUsername())
                        .password(passwordEncoder.encode(testUserDTO.getPassword()))
                        .name(testUserDTO.getName())
                        .email(testUserDTO.getEmail())
                        .role(testUserDTO.getRole())
                        .active(true)
                        .build()
        );

        UserDetails authenticatedUser = authService.loginUser(testUserDTO);
        Authentication authentication = new UsernamePasswordAuthenticationToken(authenticatedUser, null, authenticatedUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public String getRequestTokenForTest(UserDTO userDTO) throws IllegalAccessException {
        UserDetails authenticatedUser = authService.loginUser(userDTO);

        return jwtService.generateToken(authenticatedUser);
    }

    @AfterAll
    public void clean() {
        userRepository.deleteAll();
    }
}
