package de.vd40xu.smilebase.service.integration;

import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.model.emuns.UserRole;
import de.vd40xu.smilebase.repository.UserRepository;
import de.vd40xu.smilebase.service.UserService;
import de.vd40xu.smilebase.service.config.AuthContextConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest extends AuthContextConfiguration {

    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;

    User testUser = User.builder()
                        .id(1L)
                            .username("test")
                            .password("test")
                            .name("testName")
                            .email("test@email.domain")
                            .role(UserRole.RECEPTIONIST)
                            .active(true)
                            .build();

    UserDetails testUserDetails = org.springframework.security.core.userdetails.User.builder()
                                    .username(testUser.getUsername())
                                    .password(testUser.getPassword())
                                    .disabled(testUser.isActive())
                                    .build();

    @BeforeAll
    public void setUp() { userRepository.save(testUser); }

    @Test
    @DisplayName("Integration > load saved user by username")
    void test1() {
        UserDetails userDetails = userService.loadUserByUsername("test");

        assertEquals(userDetails.getUsername(), testUser.getUsername());
        assertEquals(userDetails.getPassword(), testUser.getPassword());
    }

    @Test
    @DisplayName("Integration > load saved user from principal")
    void test2() {
        when(authentication.getPrincipal()).thenReturn(testUserDetails);

        Optional<User> userData = userService.loadUserFromPrincipal(authentication.getPrincipal());

        assertFalse(userData.isEmpty());
        assertEquals(testUser.getUsername(), userData.get().getUsername());
        assertEquals(testUser.getPassword(), userData.get().getPassword());
        assertEquals(testUser.isActive(), userData.get().isActive());
    }
}
