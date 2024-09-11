package de.vd40xu.smilebase.service.interfaces;

import de.vd40xu.smilebase.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface IUserService extends UserDetailsService {
    UserDetails loadUserByUsername(String username);
    Optional<User> loadUserFromPrincipal(Object principal);
}
