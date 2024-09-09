package de.vd40xu.smilebase.service;

import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.repository.UserRepository;
import de.vd40xu.smilebase.service.interfaces.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> authUser = userRepository.findByUsername(username);
        if (authUser.isEmpty()) {
            throw new UsernameNotFoundException(username);
        } else {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(authUser.get().getUsername())
                    .password(authUser.get().getPassword())
                    .disabled(!authUser.get().isActive())
                    .build();
        }
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(this);
        authProvider.setPasswordEncoder(passwordEncoder);

        return authProvider;
    }

    public Optional<User> loadUserFromPrincipal(Object principal) {
        return userRepository.findByUsername(((UserDetails) principal).getUsername());
    }
}