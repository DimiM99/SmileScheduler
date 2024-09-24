package de.vd40xu.smilebase.repository.integraton;

import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.model.emuns.UserRole;
import de.vd40xu.smilebase.repository.UserRepository;
import de.vd40xu.smilebase.repository.config.IntegrationRepositoryTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest extends IntegrationRepositoryTest {

    @Autowired private UserRepository userRepository;

    User testUser;

    User testUser2 = User.builder()
                        .username("test2")
                        .password("new")
                        .name("testName2")
                        .email("newtest2@email.domain")
                        .role(UserRole.RECEPTIONIST)
                        .active(true)
                        .build();

    @BeforeAll
    void setUp() {
        testUser = userRepository.save(
                User.builder()
                    .username("test")
                    .password("test")
                    .name("testName")
                    .email("test@email.domain")
                    .role(UserRole.RECEPTIONIST)
                    .active(true)
                    .build()
        );
    }

    @Test
    @DisplayName("Integration > should find the testUser in the database")
    void test1() {
        assertTrue(userRepository.findByUsername(testUser.getUsername()).isPresent());
        User user = userRepository.findByUsername(testUser.getUsername()).get();

        assertEquals(testUser.getId(), user.getId());
        assertEquals(testUser.getUsername(), user.getUsername());
        assertEquals(testUser.getPassword(), user.getPassword());
        assertEquals(testUser.getName(), user.getName());
        assertEquals(testUser.getEmail(), user.getEmail());
        assertEquals(testUser.getRole(), user.getRole());
        assertTrue(user.isActive());
    }

    @Test
    @DisplayName("Integration > should save a new user in the database")
    void test2() {
        userRepository.save(testUser2);

        assertEquals(2, userRepository.count());

        assertTrue(userRepository.findByUsername(testUser2.getUsername()).isPresent());
        User newUser = userRepository.findByUsername(testUser2.getUsername()).get();

        assertEquals(testUser2.getId(), newUser.getId());
        assertEquals(testUser2.getUsername(), newUser.getUsername());
        assertEquals(testUser2.getPassword(), newUser.getPassword());
        assertEquals(testUser2.getName(), newUser.getName());
        assertEquals(testUser2.getEmail(), newUser.getEmail());
        assertEquals(testUser2.getRole(), newUser.getRole());
        assertTrue(newUser.isActive());

    }

    @Test
    @DisplayName("Integration > should update the testUser2 in the database")
    void test3() {

        testUser2.setPassword("new");
        testUser2.setEmail("newtest2@email.domain");

        userRepository.save(testUser2);

        assertTrue(userRepository.findByUsername(testUser2.getUsername()).isPresent());
        User updatedUser2 = userRepository.findByUsername(testUser2.getUsername()).get();

        assertEquals(2, userRepository.count());
        assertEquals(testUser2.getId(), updatedUser2.getId());
        assertEquals(testUser2.getUsername(), updatedUser2.getUsername());
        assertEquals(testUser2.getPassword(), updatedUser2.getPassword());
        assertEquals(testUser2.getName(), updatedUser2.getName());
        assertEquals(testUser2.getEmail(), updatedUser2.getEmail());
        assertEquals(testUser2.getRole(), updatedUser2.getRole());
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