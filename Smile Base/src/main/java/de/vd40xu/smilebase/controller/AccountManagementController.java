package de.vd40xu.smilebase.controller;

import de.vd40xu.smilebase.dto.UserDTO;
import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.service.AMService;
import de.vd40xu.smilebase.service.interfaces.IAccountManagement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@Controller
public class AccountManagementController {

    private final IAccountManagement accountManagementService;

    public AccountManagementController(AMService accountManagementService) {
        this.accountManagementService = accountManagementService;
    }

    @GetMapping("/account-management/users")
    public List<User> getUsers() throws IllegalAccessException {
        return accountManagementService.getAllUsers();
    }

    @PostMapping("/account-management/user")
    public User createUser(UserDTO user) throws IllegalAccessException {
        return accountManagementService.createOrUpdateUser(user, true);
    }

    @PutMapping("/account-management/user")
    public User updateUser(UserDTO user) throws IllegalAccessException {
        return accountManagementService.createOrUpdateUser(user, false);
    }

    @DeleteMapping("/account-management/user")
    public ResponseEntity<String> deleteUser(UserDTO user) throws IllegalAccessException {
        User res = accountManagementService.deleteUser(user);
        return res != null ?
                ResponseEntity.status(HttpStatus.GONE).body("User deleted") :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }
}
