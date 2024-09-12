package de.vd40xu.smilebase.config;

import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.model.emuns.UserRole;
import de.vd40xu.smilebase.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfig {

    final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder =  new BCryptPasswordEncoder();

    public ApplicationConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init(){
        createTheAdminUser();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return passwordEncoder;
    }

    // TODO: make this configurable via application.properties
    private void createTheAdminUser() {
        if(userRepository.findByUsername("admin").isPresent()){
            return;
        }
        User admin = User.builder()
                         .username("adminUser")
                         .password(passwordEncoder.encode("q2e13drR23e!"))
                         .name("Default Admin User")
                         .email("admin2@email.domain")
                         .role(UserRole.ADMIN)
                         .active(true)
                         .build();
        userRepository.save(admin);
    }
}
