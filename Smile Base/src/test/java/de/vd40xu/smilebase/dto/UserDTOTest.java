package de.vd40xu.smilebase.dto;

import de.vd40xu.smilebase.model.emuns.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDTOTest {

    @Test
    @DisplayName("Unit > test no args constructor")
    void testNoArgsConstructor() throws IllegalAccessException {
        UserDTO userDTO = new UserDTO();
        assertNotNull(userDTO);
        for (var field : userDTO.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            assertNull(field.get(userDTO));
        }
    }

    @Test
    @DisplayName("Unit > test all args constructor")
    void testAllArgsConstructor() {
        UserDTO userDTO = new UserDTO(
                2L,
                "newUserName",
                "newPassword",
                "newName",
                "new@mail.domain",
                UserRole.RECEPTIONIST);
        assertNotNull(userDTO);
        assertEquals(2L, userDTO.getId());
        assertEquals("newUserName", userDTO.getUsername());
        assertEquals("newPassword", userDTO.getPassword());
        assertEquals("newName", userDTO.getName());
        assertEquals("new@mail.domain", userDTO.getEmail());
        assertEquals(UserRole.RECEPTIONIST, userDTO.getRole());
    }

    @Test
    @DisplayName("Unit > test two args constructor")
    void testTwoArgsConstructor() {
        UserDTO userDTO = new UserDTO("newUserName", "newPassword");
        assertNotNull(userDTO);
        assertNull(userDTO.getId());
        assertEquals("newUserName", userDTO.getUsername());
        assertEquals("newPassword", userDTO.getPassword());
        assertNull(userDTO.getName());
        assertNull(userDTO.getEmail());
        assertNull(userDTO.getRole());
    }
}