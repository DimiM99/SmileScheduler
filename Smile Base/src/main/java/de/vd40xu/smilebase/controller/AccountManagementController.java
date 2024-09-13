package de.vd40xu.smilebase.controller;

import de.vd40xu.smilebase.dto.UserDTO;
import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.service.AMService;
import de.vd40xu.smilebase.service.interfaces.IAccountManagement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AccountManagementController {

    private final IAccountManagement accountManagementService;

    public AccountManagementController(AMService accountManagementService) {
        this.accountManagementService = accountManagementService;
    }

    @GetMapping("/account-management/users")
    public ResponseEntity<List<User>> getUsers() throws IllegalAccessException {
        return ResponseEntity.ok(accountManagementService.getAllUsers());
    }

    @PostMapping("/account-management/user")
    public ResponseEntity<User> createUser(@RequestBody UserDTO user) throws IllegalAccessException {
        return ResponseEntity.ok(accountManagementService.createOrUpdateUser(user, true));
    }

    @PutMapping("/account-management/user")
    public ResponseEntity<User> updateUser(@RequestBody UserDTO user) throws IllegalAccessException {
        return ResponseEntity.ok(accountManagementService.createOrUpdateUser(user, false));
    }

    @DeleteMapping("/account-management/user")
    public ResponseEntity<String> deleteUser(@RequestBody UserDTO user) throws IllegalAccessException {
        User res = accountManagementService.deleteUser(user);
        return res != null ?
                ResponseEntity.status(HttpStatus.GONE).body("User deleted") :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }
}
