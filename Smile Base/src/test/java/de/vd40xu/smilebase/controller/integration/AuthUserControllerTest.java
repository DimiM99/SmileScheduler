package de.vd40xu.smilebase.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.vd40xu.smilebase.controller.config.ControllerIntegrationTest;
import de.vd40xu.smilebase.dto.UserDTO;
import de.vd40xu.smilebase.model.emuns.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthUserControllerTest extends ControllerIntegrationTest {

    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("Integration > try getting the registered user with an invalid token")
    void test1() throws Exception {
        mockMvc.perform(get("/user")
                .header("Authorization", "Bearer " + "invalidRequestToken"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid JWT token"));
    }

    @Test
    @DisplayName("Integration > try getting the registered user without a token")
    void test2() throws Exception {
        mockMvc.perform(get("/user")).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Integration > try logging in with the registered user")
    void test3() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.expiresIn").isNumber());
    }

    @Test
    @DisplayName("Integration > try logging in with a non-existing user")
    void test4() throws Exception {
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
    void test5() throws Exception {
        testUserDTO.setPassword("invalidPassword");

        mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testUserDTO)))
                .andExpect(status().isForbidden());
    }
}
