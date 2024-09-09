package de.vd40xu.smilebase.repository.integraton;

import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.model.emuns.UserRole;
import de.vd40xu.smilebase.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class UserRepositoryTest {

    @Autowired private UserRepository userRepository;

    @Autowired EntityManager entityManager;

    User testUser = User.builder()
                        .id(1L)
                            .username("test")
                            .password("test")
                            .name("testName")
                            .email("test@email.domain")
                            .role(UserRole.RECEPTIONIST)
                            .active(true)
                            .build();

    User testUser2 = User.builder()
                        .id(2L)
                        .username("test2")
                        .password("new")
                        .name("testName2")
                        .email("newtest2@email.domain")
                        .role(UserRole.RECEPTIONIST)
                        .active(true)
                        .build();

    @BeforeEach
    void setUp() { userRepository.save(testUser); }

    @Test
    @DisplayName("Integration > should find the testUser in the database")
    void test1() {
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

    @Test
    @DisplayName("Integration > should save a new user in the database")
    void test2() {
        User testUser2 = User.builder()
                .id(2L)
                .username("test2")
                .password("test2")
                .name("testName2")
                .email("test2@email.domain")
                .role(UserRole.RECEPTIONIST)
                .active(true)
                .build();

        userRepository.save(testUser2);

        assertEquals(2, userRepository.count());

        assertTrue(userRepository.findByUsername("test2").isPresent());
        User newUser = userRepository.findByUsername("test2").get();

        assertEquals(2L, newUser.getId());
        assertEquals("test2", newUser.getUsername());
        assertEquals("test2", newUser.getPassword());
        assertEquals("testName2", newUser.getName());
        assertEquals("test2@email.domain", newUser.getEmail());
        assertEquals(UserRole.RECEPTIONIST, newUser.getRole());
        assertTrue(newUser.isActive());

    }

    @Test
    @DisplayName("Integration > should update the testUser2 in the database")
    void test3() {

        testUser2.setPassword("new");
        testUser2.setEmail("newtest2@email.domain");

        userRepository.save(testUser2);

        assertTrue(userRepository.findByUsername("test2").isPresent());
        User updatedUser2 = userRepository.findByUsername("test2").get();

        assertEquals(2, userRepository.count());
        assertEquals(2L, updatedUser2.getId());
        assertEquals("test2", updatedUser2.getUsername());
        assertEquals("new", updatedUser2.getPassword());
        assertEquals("testName2", updatedUser2.getName());
        assertEquals("newtest2@email.domain", updatedUser2.getEmail());
        assertEquals(UserRole.RECEPTIONIST, updatedUser2.getRole());
        assertTrue(updatedUser2.isActive());
    }

    @Test
    @DisplayName("Integration > should delete the testUser from the database")
    void test4() {
        userRepository.delete(testUser);

        assertFalse(userRepository.findByUsername("test").isPresent());
        assertEquals(1, userRepository.count());
    }
}