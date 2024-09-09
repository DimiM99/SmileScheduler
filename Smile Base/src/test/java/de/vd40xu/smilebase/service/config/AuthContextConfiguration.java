package de.vd40xu.smilebase.service.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public abstract class AuthContextConfiguration {

    @Mock public Authentication authentication;
    @Mock public SecurityContext securityContext;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder
                .setContext(securityContext);

        lenient().when(securityContext.getAuthentication())
                 .thenReturn(authentication);
    }

    @Configuration
    static class TestClassConfig {}

}
