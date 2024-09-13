package de.vd40xu.smilebase.service.integration;

import de.vd40xu.smilebase.dto.UserDTO;
import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.model.emuns.UserRole;
import de.vd40xu.smilebase.repository.UserRepository;
import de.vd40xu.smilebase.service.AMService;
import de.vd40xu.smilebase.service.UserService;
import de.vd40xu.smilebase.service.config.AuthContextConfiguration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AMServiceTest extends AuthContextConfiguration {

    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;

    @Autowired private AMService amService;

    User receptionistUser = User.builder()
                                .username("testReceptionist")
                                .password("testReceptionist")
                                .name("Test Receptionist 1")
                                .email("testReceptionins@email.domain")
                                .role(UserRole.RECEPTIONIST)
                                .active(true)
                                .build();

    UserDTO testUser = new UserDTO(
            2L,
            "testUserUsername",
            "testUserPassword",
            "Test User",
            "newTestUser@email.domain",
            UserRole.RECEPTIONIST);

    @BeforeAll
    public void init() {
        userRepository.save(receptionistUser);
    }

    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    private void authenticateTestUser(Boolean isAdmin) {
        if (isAdmin) {
            lenient().when(authentication.getPrincipal())
                    .thenReturn(userService.loadUserByUsername("adminUser"));
        } else {
            lenient().when(authentication.getPrincipal())
                    .thenReturn(userService.loadUserByUsername("testReceptionist"));
        }
    }

    @Test
    @DisplayName("Integration > check configuration, test Receptionist and Admin should be created by ApplicationConfig and this.init()")
    void test1() {
        Optional<User> user = userRepository.findByUsername("adminUser");
        assertTrue(user.isPresent());
        assertEquals(2, userRepository.count());
    }

    @Test
    @DisplayName("Integration > test authenticated and authorized access")
    void test2() {
        authenticateTestUser(true);
        assertDoesNotThrow(() -> amService.getAllUsers());
        assertDoesNotThrow(() -> amService.createOrUpdateUser(testUser, true));
        assertDoesNotThrow(() -> amService.createOrUpdateUser(testUser, false));
        assertDoesNotThrow(() -> amService.deleteUser(testUser));
    }

    @Test
    @DisplayName("Integration > try accessing anything with being authenticated")
    void test3() {
        assertThrows(NullPointerException.class, () -> amService.getAllUsers());
        assertThrows(NullPointerException.class, () -> amService.createOrUpdateUser(testUser, true));
        assertThrows(NullPointerException.class, () -> amService.createOrUpdateUser(testUser, false));
        assertThrows(NullPointerException.class, () -> amService.deleteUser(testUser));
    }

    @Test
    @DisplayName("Integration > test authenticated and unauthorized access")
    void test4() {
        authenticateTestUser(false);
        assertThrows(IllegalAccessException.class, () -> amService.getAllUsers());
        assertThrows(IllegalAccessException.class, () -> amService.createOrUpdateUser(testUser, true));
        assertThrows(IllegalAccessException.class, () -> amService.createOrUpdateUser(testUser, false));
        assertThrows(IllegalAccessException.class, () -> amService.deleteUser(testUser));
    }

    @Test
    @DisplayName("Integration > test creating a user")
    void test5() {
        authenticateTestUser(true);
        assertDoesNotThrow(() -> amService.createOrUpdateUser(testUser, true));
        Optional<User> user = userRepository.findByUsername(testUser.getUsername());
        assertTrue(user.isPresent());
    }

    @ParameterizedTest
    @DisplayName("Integration > test updating a user")
    @MethodSource("userUpdateOptions")
    void test6(UserDTO userDTO) {
        Optional<User> currentUser = userRepository.findByUsername(userDTO.getUsername());
        assertTrue(currentUser.isPresent());
        authenticateTestUser(true);
        assertDoesNotThrow(() -> amService.createOrUpdateUser(userDTO, false));
        Optional<User> updatedUser = userRepository.findByUsername(userDTO.getUsername());
        assertTrue(updatedUser.isPresent());
        if (userDTO.getPassword() != null) {
            assertNotEquals(currentUser.get().getPassword(), updatedUser.get().getPassword());
        }
        if (userDTO.getEmail() != null) {
            assertNotEquals(currentUser.get().getEmail(), updatedUser.get().getEmail());
        }
    }

    public Stream<Arguments> userUpdateOptions() {
        return Stream.of(
                Arguments.of(
                            Named.of("update only password",
                                    new UserDTO(
                                        testUser.getUsername(),
                                        "newPassword"
                                    )
                            )
                ),
                Arguments.of(Named.of("update only email",
                                    new UserDTO(
                                        testUser.getId(),
                                        testUser.getUsername(),
                                        null,
                                        testUser.getName(),
                                        "newEmail@mail.domain",
                                        testUser.getRole()
                                    )
                ))
        );
    }
}