package de.vd40xu.smilebase.controller.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.vd40xu.smilebase.config.security.JwtAuthenticationFilter;
import de.vd40xu.smilebase.controller.AccountManagementController;
import de.vd40xu.smilebase.dto.UserDTO;
import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.model.emuns.UserRole;
import de.vd40xu.smilebase.service.AMService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AccountManagementController.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountManagementControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private AMService accountManagementService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
            return Mockito.mock(JwtAuthenticationFilter.class);
        }
    }

    @Test
    @DisplayName("Unit > GET /account-management/users - Success")
    void testGetUsersSuccess() throws Exception {
        List<User> users = Arrays.asList(
            new User(1L, "user1", "pass1", "User One", "user1@example.com", UserRole.DOCTOR, true),
            new User(2L, "user2", "pass2", "User Two", "user2@example.com", UserRole.RECEPTIONIST, true)
        );
        when(accountManagementService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/account-management/users"))
               .andExpect(status().isOk())
               .andExpect(content().json(objectMapper.writeValueAsString(users)));
    }

    @Test
    @DisplayName("Unit > GET /account-management/users - Forbidden")
    void testGetUsersForbidden() throws Exception {
        when(accountManagementService.getAllUsers()).thenThrow(new IllegalAccessException("Forbidden"));

        mockMvc.perform(get("/account-management/users"))
               .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Unit > POST /account-management/user - Success")
    void testCreateUserSuccess() throws Exception {
        UserDTO userDTO = new UserDTO("newuser", "password");
        User createdUser = new User(3L, "newuser", "encodedPassword", "New User", "newuser@example.com", UserRole.DOCTOR, true);

        when(accountManagementService.createOrUpdateUser(any(UserDTO.class), eq(true))).thenReturn(createdUser);

        mockMvc.perform(post("/account-management/user")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(userDTO)))
               .andExpect(status().isOk())
               .andExpect(content().json(objectMapper.writeValueAsString(createdUser)));
    }

    @Test
    @DisplayName("Unit > POST /account-management/user - Forbidden")
    void testCreateUserForbidden() throws Exception {
        UserDTO userDTO = new UserDTO("newuser", "password");

        when(accountManagementService.createOrUpdateUser(any(UserDTO.class), eq(true)))
            .thenThrow(new IllegalAccessException("Forbidden"));

        mockMvc.perform(post("/account-management/user")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(userDTO)))
               .andExpect(status().isForbidden())
               .andExpect(content().string("Forbidden"));
    }

    @Test
    @DisplayName("Unit > PUT /account-management/user - Success")
    void testUpdateUserSuccess() throws Exception {
        UserDTO userDTO = new UserDTO("existinguser", "newpassword");
        User updatedUser = new User(1L, "existinguser", "encodedNewPassword", "Existing User", "existing@example.com", UserRole.DOCTOR, true);

        when(accountManagementService.createOrUpdateUser(any(UserDTO.class), eq(false))).thenReturn(updatedUser);

        mockMvc.perform(put("/account-management/user")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(userDTO)))
               .andExpect(status().isOk())
               .andExpect(content().json(objectMapper.writeValueAsString(updatedUser)));
    }

    @Test
    @DisplayName("Unit > PUT /account-management/user - Forbidden")
    void testUpdateUserForbidden() throws Exception {
        UserDTO userDTO = new UserDTO("existinguser", "newpassword");

        when(accountManagementService.createOrUpdateUser(any(UserDTO.class), eq(false)))
            .thenThrow(new IllegalAccessException("Forbidden"));

        mockMvc.perform(put("/account-management/user")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(userDTO)))
               .andExpect(status().isForbidden())
               .andExpect(content().string("Forbidden"));
    }

    @Test
    @DisplayName("Unit > PUT /account-management/user - Not Found")
    void testUpdateUserNotFound() throws Exception {
        UserDTO userDTO = new UserDTO("nonexistentuser", "password");

        when(accountManagementService.createOrUpdateUser(any(UserDTO.class), eq(false)))
            .thenThrow(new UsernameNotFoundException("User not found"));

        mockMvc.perform(put("/account-management/user")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(userDTO)))
               .andExpect(status().isNotFound())
               .andExpect(content().string("User not found"));
    }

    @Test
    @DisplayName("Unit > DELETE /account-management/user - Success")
    void testDeleteUserSuccess() throws Exception {
        UserDTO userDTO = new UserDTO("userToDelete", "password");

        when(accountManagementService.deleteUser(any(UserDTO.class))).thenReturn(new User());

        mockMvc.perform(delete("/account-management/user")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(userDTO)))
               .andExpect(status().isOk())
               .andExpect(content().string("User deleted"));
    }

    @Test
    @DisplayName("Unit > DELETE /account-management/user - Forbidden")
    void testDeleteUserForbidden() throws Exception {
        UserDTO userDTO = new UserDTO("userToDelete", "password");

        doThrow(new IllegalAccessException("Forbidden"))
            .when(accountManagementService).deleteUser(any(UserDTO.class));

        mockMvc.perform(delete("/account-management/user")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(userDTO)))
               .andExpect(status().isForbidden())
               .andExpect(content().string("Forbidden"));
    }

    @Test
    @DisplayName("Unit > DELETE /account-management/user - Not Found")
    void testDeleteUserNotFound() throws Exception {
        UserDTO userDTO = new UserDTO("nonexistentuser", "password");

        doThrow(new UsernameNotFoundException("User not found"))
            .when(accountManagementService).deleteUser(any(UserDTO.class));

        mockMvc.perform(delete("/account-management/user")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(userDTO)))
               .andExpect(status().isNotFound())
               .andExpect(content().string("User not found"));
    }
}
