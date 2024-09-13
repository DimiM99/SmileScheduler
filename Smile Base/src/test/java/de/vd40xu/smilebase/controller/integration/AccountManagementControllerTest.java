package de.vd40xu.smilebase.controller.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.vd40xu.smilebase.controller.config.ControllerIntegrationTest;
import de.vd40xu.smilebase.dto.UserDTO;
import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.model.emuns.UserRole;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountManagementControllerTest extends ControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    public UserDTO newTestUserDTO;

    public UserDTO testAdminUserDTO = new UserDTO(
                "adminUser",
                "q2e13drR23e!"
    );

    private String requestToken = "";

    private Object unwrapResponse(
            MvcResult result,
            JavaType collectionType
    ) throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(
                result.getResponse().getContentAsString(),
                collectionType
        );
    }

    @BeforeAll
    public void seUp() throws IllegalAccessException {
        super.setUp();
    }

    @BeforeEach
    public void setUp() {
        newTestUserDTO = new UserDTO(
                2L,
                "testUser2",
                "testPassword2",
                "testName2",
                "test2@email.domain",
                UserRole.RECEPTIONIST);
    }

    @Test
    @DisplayName("Integration > try getting all users")
    void test1() throws Exception {
        requestToken = getRequestTokenForTest(testAdminUserDTO);
        MvcResult result = mockMvc.perform(get("/account-management/users")
                .header("Authorization", "Bearer " + requestToken))
                .andExpect(status().isOk()).andReturn();
        Object objectResultData = unwrapResponse(result,
                objectMapper.getTypeFactory().constructCollectionType(List.class, User.class)
        );
        assertNotNull(objectResultData);
        List<User> resultData = (List<User>) objectResultData;
        assertNotNull(resultData);
        assertEquals(1, resultData.size());
    }

    @Test
    @DisplayName("Integration > try creating a user")
    void test2() throws Exception {
        MvcResult result = mockMvc.perform(post("/account-management/user")
                .header("Authorization", "Bearer " + requestToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newTestUserDTO)))
                .andExpect(status().isOk()).andReturn();
        Object resultData = unwrapResponse(
                result,
                objectMapper.getTypeFactory().constructType(User.class)
        );
        assertNotNull(resultData);
        User user = (User) resultData;
        assertEquals(newTestUserDTO.getUsername(), user.getUsername());
    }

    @Test
    @DisplayName("Integration > registering user with existing username, (register same user again)")
    void test3() throws Exception {
        mockMvc.perform(post("/account-management/user")
                .header("Authorization", "Bearer " + requestToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newTestUserDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Integration > try updating a user")
    void test4() throws Exception {
        UserDTO currentUser = newTestUserDTO;
        newTestUserDTO.setEmail("newTest2@email.domain");
        MvcResult result = mockMvc.perform(put("/account-management/user")
                .header("Authorization", "Bearer " + requestToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newTestUserDTO)))
                .andExpect(status().isOk()).andReturn();
        Object resultData = unwrapResponse(
                result,
                objectMapper.getTypeFactory().constructType(User.class)
        );
        assertNotNull(resultData);
        User user = (User) resultData;
        assertEquals(currentUser.getEmail(), user.getEmail());
    }

    @Test
    @DisplayName("Integration > updating user that does not exist")
    void test5() throws Exception {
        UserDTO test = newTestUserDTO;
        test.setUsername("nonExistentUser");
        mockMvc.perform(put("/account-management/user")
                .header("Authorization", "Bearer " + requestToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newTestUserDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Integration > try deleting a user that does not exist")
    void test6() throws Exception {
        UserDTO test = newTestUserDTO;
        test.setUsername("nonExistentUser");
        mockMvc.perform(delete("/account-management/user")
                .header("Authorization", "Bearer " + requestToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(test)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Integration > try deleting a user")
    void test7() throws Exception {
        mockMvc.perform(delete("/account-management/user")
                .header("Authorization", "Bearer " + requestToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newTestUserDTO)))
                .andExpect(status().isOk());
    }
}