package de.vd40xu.smilebase.service.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:test_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.username=sa",
    "spring.datasource.password=password",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
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
