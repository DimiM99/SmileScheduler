package de.vd40xu.smilebase.controller;

import de.vd40xu.smilebase.dto.LoginResponseDTO;
import de.vd40xu.smilebase.dto.UserDTO;
import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.service.AuthService;
import de.vd40xu.smilebase.service.UserService;
import de.vd40xu.smilebase.service.interfaces.IAuthService;
import de.vd40xu.smilebase.service.interfaces.IUserService;
import de.vd40xu.smilebase.service.utility.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Controller
public class AuthUserController {

    private final IUserService userService;
    private final IAuthService authService;
    private final JwtService jwtService;

    public AuthUserController(
            UserService userService,
            AuthService authService,
            JwtService jwtService
    ) {
        this.userService = userService;
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Object> loginUser(@RequestBody UserDTO userDto) {
        UserDetails authenticatedUser;
        try {
            authenticatedUser = authService.loginUser(userDto);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

        String jwtToken = jwtService.generateToken(authenticatedUser);

        return ResponseEntity.status(HttpStatus.OK)
                             .body(LoginResponseDTO.builder()
                                                   .token(jwtToken)
                                                   .expiresIn(jwtService.getExpirationTime())
                                                   .build()
                             );
    }

    @GetMapping("/user")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUser = (UserDetails) authentication.getPrincipal();
        Optional<User> res = userService.loadUserFromPrincipal(currentUser);
        if (res.isPresent()) {
            res.get().setPassword(null);
            return ResponseEntity.ok(res.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
