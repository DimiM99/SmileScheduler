package de.vd40xu.smilebase.service.unit;

import de.vd40xu.smilebase.dto.UserDTO;
import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.model.emuns.UserRole;
import de.vd40xu.smilebase.repository.UserRepository;
import de.vd40xu.smilebase.service.AMService;
import de.vd40xu.smilebase.service.UserService;
import de.vd40xu.smilebase.service.config.AuthContextConfiguration;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AMServiceTest extends AuthContextConfiguration {

    @Mock private UserService userService;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private AMService amService;

    User userMakingTheRequest = User.builder()
            .id(1L)
            .username("admin")
            .password("admin")
            .name("Admin")
            .email("admin@email.domain")
            .role(UserRole.ADMIN)
            .active(true)
            .build();

    UserDTO testUserDTO = new UserDTO(3L,
            "testUser",
            "testPassword",
            "testName",
            "testEmail",
            UserRole.RECEPTIONIST
    );

    @BeforeEach
    public void init() {
        super.setUp();
    }

    private void mockAuthorisedUser() {
        when(authentication.getPrincipal()).thenReturn(userMakingTheRequest);
        when(userService.loadUserFromPrincipal(any())).thenReturn(Optional.of(userMakingTheRequest));
    }

    private void mockUnauthorisedUser() {
        User unauthorisedUser = User.builder()
                                    .id(2L)
                                    .username("receptionist")
                                    .password("receptionist")
                                    .name("Receptionist")
                                    .email("user1@email.domain")
                                    .role(UserRole.RECEPTIONIST)
                                    .active(true)
                                    .build();
        lenient().when(authentication.getPrincipal()).thenReturn((Object) unauthorisedUser);
        lenient().when(userService.loadUserFromPrincipal(any())).thenReturn(Optional.of(unauthorisedUser));
    }

    @Test
    @DisplayName("Unit > try accessing anything without being an admin")
    public void test1() {
        mockUnauthorisedUser();
        assertThrows(IllegalAccessException.class, () -> amService.getAllUsers());
        assertThrows(IllegalAccessException.class, () -> amService.createOrUpdateUser(testUserDTO, true));
        assertThrows(IllegalAccessException.class, () -> amService.createOrUpdateUser(testUserDTO, false));
        assertThrows(IllegalAccessException.class, () -> amService.deleteUser(testUserDTO));
    }

    @Test
    @DisplayName("Unit > try accessing anything without being a user")
    public void test2() {
        when(authentication.getPrincipal()).thenReturn(Optional.empty());
        when(userService.loadUserFromPrincipal(any())).thenReturn(Optional.empty());
        assertThrows(IllegalAccessException.class, () -> amService.getAllUsers());
        assertThrows(IllegalAccessException.class, () -> amService.createOrUpdateUser(testUserDTO, true));
        assertThrows(IllegalAccessException.class, () -> amService.createOrUpdateUser(testUserDTO, false));
        assertThrows(IllegalAccessException.class, () -> amService.deleteUser(testUserDTO));
    }

    @Test
    @DisplayName("Unit > get all users")
    public void test3() {
        mockAuthorisedUser();
        when(userRepository.findAllWithRoleReceptionistOrDoctor()).thenReturn(List.of());
        assertDoesNotThrow(() -> amService.getAllUsers());
    }

    @Test
    @DisplayName("Unit > create user")
    public void test4() {
        mockAuthorisedUser();
        when(userRepository.save(any())).thenReturn(null);
        assertDoesNotThrow(() -> amService.createOrUpdateUser(testUserDTO, true));
    }

    @Test
    @DisplayName("Unit > update user")
    public void test5() {
        mockAuthorisedUser();
        when(userRepository.findByUsername(testUserDTO.getUsername())).thenReturn(Optional.of(User.builder().build()));
        when(userRepository.save(any())).thenReturn(null);
        assertDoesNotThrow(() -> amService.createOrUpdateUser(testUserDTO, false));
    }

    @Test
    @DisplayName("Unit > update user non-existing user")
    public void test6() {
        mockAuthorisedUser();
        when(userRepository.findByUsername(testUserDTO.getUsername())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> amService.createOrUpdateUser(testUserDTO, false));
    }

    @Test
    @DisplayName("Unit > update user without password change")
    public void test7() {
        mockAuthorisedUser();
        when(userRepository.findByUsername(any())).thenReturn(
                Optional.of(
                        User.builder()
                                .username(testUserDTO.getUsername())
                                .password(testUserDTO.getPassword())
                                .name(testUserDTO.getName())
                                .email(testUserDTO.getEmail())
                                .role(testUserDTO.getRole())
                                .build()
                )
        );
        testUserDTO.setPassword(null);
        assertDoesNotThrow(() -> amService.createOrUpdateUser(testUserDTO, false));
        verify(passwordEncoder, times(0)).encode(any());
    }

    @Test
    @DisplayName("Unit > update user without email change")
    public void test8() {
        mockAuthorisedUser();
        when(userRepository.findByUsername(any())).thenReturn(
                Optional.of(
                        User.builder()
                                .username(testUserDTO.getUsername())
                                .password(testUserDTO.getPassword())
                                .name(testUserDTO.getName())
                                .email(testUserDTO.getEmail())
                                .role(testUserDTO.getRole())
                                .build()
                )
        );
        testUserDTO.setEmail(null);
        assertDoesNotThrow(() -> amService.createOrUpdateUser(testUserDTO, false));
    }

    @Test
    @DisplayName("Unit > delete user")
    public void test9() {
        mockAuthorisedUser();
        when(userRepository.findByUsername(testUserDTO.getUsername())).thenReturn(Optional.of(User.builder().build()));
        assertDoesNotThrow(() -> amService.deleteUser(testUserDTO));
    }

    @Test
    @DisplayName("Unit > delete user non-existing user")
    public void test10() {
        mockAuthorisedUser();
        when(userRepository.findByUsername(testUserDTO.getUsername())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> amService.deleteUser(testUserDTO));
    }
}