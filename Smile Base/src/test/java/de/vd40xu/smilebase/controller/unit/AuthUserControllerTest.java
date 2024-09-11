package de.vd40xu.smilebase.controller.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.vd40xu.smilebase.controller.AuthUserController;
import de.vd40xu.smilebase.dto.UserDTO;
import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.model.emuns.UserRole;
import de.vd40xu.smilebase.service.AuthService;
import de.vd40xu.smilebase.service.UserService;
import de.vd40xu.smilebase.service.utility.JwtService;
import jakarta.transaction.TransactionalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthUserController.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthUserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private UserService userService;
    @MockBean private AuthService authService;
    @MockBean private JwtService jwtService;

    private User testUser;
    private UserDTO testUserDTO;
    private UserDetails testUserDetails;

    @BeforeEach
    public void setUp() {
        testUserDTO = new UserDTO(
                1L,
                "testUser",
                "testPassword",
                "testName",
                "test@email.domain",
                UserRole.RECEPTIONIST
        );
        testUser = User.builder()
                       .id(testUserDTO.getId())
                       .username(testUserDTO.getUsername())
                       .password(testUserDTO.getPassword())
                       .name(testUserDTO.getName())
                       .email(testUserDTO.getEmail())
                       .role(testUserDTO.getRole())
                       .active(true)
                       .build();

        testUserDetails = org.springframework.security.core.userdetails.User.builder()
                          .username(testUserDTO.getUsername())
                          .password(testUserDTO.getPassword())
                          .disabled(!testUser.isActive())
                          .build();
    }

    @Test
    @DisplayName("Unit > test \"/auth/register\" endpoint with a new user")
    void test1() throws Exception {
        doNothing().when(authService).registerUser(testUserDTO);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDTO)))
                .andExpect(status().isCreated());

        verify(authService, times(1)).registerUser(any(UserDTO.class));
    }

    @Test
    @DisplayName("Unit > test \"/auth/register\" endpoint with an existing user")
    void test2() throws Exception {
        doThrow(IllegalArgumentException.class).when(authService).registerUser(any(UserDTO.class));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDTO)))
                .andExpect(status().isConflict());

        verify(authService, times(1)).registerUser(any(UserDTO.class));
    }

    @Test
    @DisplayName("Unit > test \"/auth/register\" endpoint with wrong data")
    void test3() throws Exception {
        doThrow(TransactionalException.class).when(authService).registerUser(any(UserDTO.class));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserDTO())))
                .andExpect(status().isInternalServerError());

        verify(authService, times(1)).registerUser(any(UserDTO.class));
    }

    @Test
    @DisplayName("Unit > test \"/auth/login\" endpoint with valid credentials")
    void test4() throws Exception {
        when(authService.loginUser(any(UserDTO.class))).thenReturn(testUserDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("testToken");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDTO)))
                .andExpect(status().isOk());

        verify(authService, times(1)).loginUser(any(UserDTO.class));
        verify(jwtService, times(1)).generateToken(any(UserDetails.class));
    }

    @Test
    @DisplayName("Unit > test \"/auth/login\" endpoint with valid credentials but user inactive")
    void test5() throws Exception {
        doThrow(IllegalAccessException.class).when(authService).loginUser(any(UserDTO.class));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDTO)))
                .andExpect(status().isUnauthorized());

        verify(authService, times(1)).loginUser(any(UserDTO.class));
        verify(jwtService, times(0)).generateToken(any(UserDetails.class));
    }

    @Test
    @DisplayName("Unit > test \"/auth/login\" endpoint with invalid credentials")
    void test6() throws Exception {
        when(authService.loginUser(any(UserDTO.class))).thenThrow(UsernameNotFoundException.class);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDTO)))
                .andExpect(status().isNotFound());

        verify(authService, times(1)).loginUser(any(UserDTO.class));
    }

    @Test
    @DisplayName("Unit > test \"/user\" endpoint with a valid token")
    void test7() throws Exception {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUserDetails);

        SecurityContextHolder.setContext(securityContext);

        when(userService.loadUserFromPrincipal(testUserDetails)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assert content.contains(testUserDTO.getUsername());
                    assert !content.contains(testUserDTO.getPassword());
                });

        verify(userService, times(1)).loadUserFromPrincipal(testUserDetails);
    }

    @Test
    @DisplayName("Unit > test \"/user\" endpoint with an invalid token")
    void test8() throws Exception {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUserDetails);

        SecurityContextHolder.setContext(securityContext);

        when(userService.loadUserFromPrincipal(testUserDetails)).thenReturn(Optional.empty());

        mockMvc.perform(get("/user"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).loadUserFromPrincipal(testUserDetails);
    }

}