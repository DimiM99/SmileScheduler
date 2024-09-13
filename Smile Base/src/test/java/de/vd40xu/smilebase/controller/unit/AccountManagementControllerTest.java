package de.vd40xu.smilebase.controller.unit;

import de.vd40xu.smilebase.controller.AccountManagementController;
import de.vd40xu.smilebase.dto.UserDTO;
import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.service.AMService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountManagementControllerTest {

    @Mock private AMService accountManagementService;

    @InjectMocks
    private AccountManagementController accountManagementController;

    @Test
    @DisplayName("Unit > test getting all users")
    void test1() throws IllegalAccessException {
        when(accountManagementService.getAllUsers()).thenReturn(new ArrayList<>());
        assertEquals(0, accountManagementController.getUsers().getBody().size());
        verify(accountManagementService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("Unit > test creating user")
    void test2() throws IllegalAccessException {
        when(accountManagementService.createOrUpdateUser(any(), eq(true))).thenReturn(null);
        assertNull(accountManagementController.createUser(null).getBody());
        verify(accountManagementService, times(1)).createOrUpdateUser(null, true);
    }

    @Test
    @DisplayName("Unit > test updating user")
    void test3() throws IllegalAccessException {
        when(accountManagementService.createOrUpdateUser(any(), eq(false))).thenReturn(null);
        assertNull(accountManagementController.updateUser(null).getBody());
        verify(accountManagementService, times(1)).createOrUpdateUser(null, false);
    }

    @Test
    @DisplayName("Unit > test deleting user")
    void test4() throws IllegalAccessException {
        when(accountManagementService.deleteUser(any())).thenReturn(User.builder().build());
        assertEquals("User deleted", accountManagementController.deleteUser(new UserDTO()).getBody());
    }

    @Test
    @DisplayName("Unit > test deleting user (not existing)")
    void test5() throws IllegalAccessException {
        when(accountManagementService.deleteUser(any())).thenReturn(null);
        assertEquals("User not found", accountManagementController.deleteUser(null).getBody());
        verify(accountManagementService, times(1)).deleteUser(null);
    }

}