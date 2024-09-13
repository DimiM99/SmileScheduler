package de.vd40xu.smilebase.controller;

import de.vd40xu.smilebase.dto.UserDTO;
import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.service.AMService;
import de.vd40xu.smilebase.service.interfaces.IAccountManagement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public ResponseEntity<List<User>> getUsers() {
        try {
            return ResponseEntity.ok(accountManagementService.getAllUsers());
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PostMapping("/account-management/user")
    public ResponseEntity<Object> createUser(@RequestBody UserDTO user) {
        try {
            return ResponseEntity.ok(accountManagementService.createOrUpdateUser(user, true));
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PutMapping("/account-management/user")
    public ResponseEntity<Object> updateUser(@RequestBody UserDTO user) {
        try {
            return ResponseEntity.ok(accountManagementService.createOrUpdateUser(user, false));
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/account-management/user")
    public ResponseEntity<String> deleteUser(@RequestBody UserDTO user) {
        try {
            accountManagementService.deleteUser(user);
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.ok("User deleted");
    }
}
