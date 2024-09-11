package de.vd40xu.smilebase.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.vd40xu.smilebase.controller.config.ControllerIntegrationTest;
import de.vd40xu.smilebase.dto.UserDTO;
import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.model.emuns.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthUserControllerTest extends ControllerIntegrationTest {

    @Autowired private ObjectMapper objectMapper;

    UserDTO newUserDTO = new UserDTO(
                2L,
                "newUserName",
                "newPassword",
                "newName",
                "new@mail.domain",
                UserRole.RECEPTIONIST);

    @Test
    @DisplayName("Integration > get the registered user")
    void test1() throws Exception {
        String requestToken = getRequestTokenForTest(testUserDTO);

        mockMvc.perform(get("/user")
                .header("Authorization", "Bearer " + requestToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUserDTO.getId()))
                .andExpect(jsonPath("$.email").value(testUserDTO.getEmail()))
                .andExpect(jsonPath("$.name").value(testUserDTO.getName()))
                .andExpect(jsonPath("$.role").value(testUserDTO.getRole().name()));
    }

    @Test
    @DisplayName("Integration > try getting the registered user with an invalid token")
    void test2() throws Exception {
        mockMvc.perform(get("/user")
                .header("Authorization", "Bearer " + "invalidRequestToken"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid JWT token"));
    }

    @Test
    @DisplayName("Integration > try getting the registered user without a token")
    void test3() throws Exception {
        mockMvc.perform(get("/user")).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Integration > try registering a new user")
    void test4() throws Exception {

        mockMvc.perform(post("/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully"));

        Optional<User> user = userRepository.findByUsername(newUserDTO.getUsername());

        assertTrue(user.isPresent());
        assertEquals(newUserDTO.getEmail(), user.get().getEmail());
        assertEquals(newUserDTO.getName(), user.get().getName());
        assertEquals(newUserDTO.getRole(), user.get().getRole());
    }

    @Test
    @DisplayName("Integration > try registering a new user with an existing username")
    void test5() throws Exception {

        mockMvc.perform(post("/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Username already taken. Please try again"));
    }

    @Test
    @DisplayName("Integration > try logging in with the registered user")
    void test6() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.expiresIn").isNumber());
    }

    @Test
    @DisplayName("Integration > try logging in with a non-existing user")
    void test7() throws Exception {
        UserDTO invalidUserDTO = new UserDTO(
                2L,
                "invalidUserName",
                "invalidPassword",
                "invalidName",
                "invalid@mail.domain",
                UserRole.RECEPTIONIST);

        mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidUserDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    @DisplayName("Integration > try logging in with an invalid password")
    void test8() throws Exception {
        newUserDTO.setPassword("invalidPassword");

        mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isForbidden());
    }
}
