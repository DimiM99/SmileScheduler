package de.vd40xu.smilebase.repository.unit;

import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.model.emuns.UserRole;
import de.vd40xu.smilebase.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    UserRepository userRepository;

    User testUser = User.builder()
                        .id(1L)
                        .username("test")
                        .password("test")
                        .name("testName")
                        .email("test@email.domain")
                        .role(UserRole.RECEPTIONIST)
                        .active(true)
                        .build();

    @Test
    @DisplayName("Unit  > should save user to database")
    void test1() {
        when(userRepository.save(testUser)).thenReturn(testUser);

        User user = userRepository.save(testUser);

        assertEquals(1L, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("test", user.getPassword());
        assertEquals("testName", user.getName());
        assertEquals("test@email.domain", user.getEmail());
        assertEquals(UserRole.RECEPTIONIST, user.getRole());
        assertTrue(user.isActive());
    }

    @Test
    @DisplayName("Unit > should find user by username")
    void test2() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(testUser));

        assertTrue(userRepository.findByUsername("test").isPresent());
        User user = userRepository.findByUsername("test").get();

        assertEquals(1L, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("test", user.getPassword());
        assertEquals("testName", user.getName());
        assertEquals("test@email.domain", user.getEmail());
        assertEquals(UserRole.RECEPTIONIST, user.getRole());
        assertTrue(user.isActive());
    }
}
