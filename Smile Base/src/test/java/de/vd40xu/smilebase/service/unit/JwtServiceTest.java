package de.vd40xu.smilebase.service.unit;

import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.service.utility.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private final String secret = "c4f2407efe874c8596662d70746ea48f937c1123ec5f77f0ac01ee6aea54c038";
    private final Key signInKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

    @InjectMocks
    private JwtService jwtService;

    private final User user = User.builder()
                                    .id(1L)
                                    .username("test")
                                    .password("test")
                                    .active(true)
                                    .build();

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", secret);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);
        userDetails = org.springframework.security.core.userdetails.User.builder()
                          .username(user.getUsername())
                          .password(user.getPassword())
                          .disabled(!user.isActive())
                          .build();
    }

    @Test
    @DisplayName("Unit > Test extracting username from token")
    void test1() {
        String token = jwtService.generateToken(userDetails);
        assertEquals(user.getUsername(), jwtService.extractUsername(token));
    }

    @Test
    @DisplayName("Unit > Test extracting claim from token")
    void test2() {
        String token = jwtService.generateToken(userDetails);
        assertEquals(user.getUsername(), jwtService.extractClaim(token, Claims::getSubject));
    }

    @Test
    @DisplayName("Unit > Test token generation")
    void test3() {
        String token = jwtService.generateToken(userDetails);
        Jws<Claims> claims = Jwts.parser()
                                 .verifyWith((SecretKey) signInKey)
                                 .build()
                                 .parseSignedClaims(token);
        assertEquals(user.getUsername(), claims.getPayload().getSubject());
    }

    @Test
    @DisplayName("Unit > Test token generation with extra claims")
    void test4() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("test", "test");
        String token = jwtService.generateToken(extraClaims, userDetails);
        Jws<Claims> claims = Jwts.parser()
                                 .verifyWith((SecretKey) signInKey)
                                 .build()
                                 .parseSignedClaims(token);
        assertEquals(user.getUsername(), claims.getPayload().getSubject());
        assertEquals("test", claims.getPayload().get("test"));
    }

    @Test
    @DisplayName("Unit > Test checking expiration time")
    void test5() {
        assertEquals(3600000L, jwtService.getExpirationTime());
    }

    @Test
    @DisplayName("Unit > Test token Validation with valid token")
    void test6() {
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    @DisplayName("Unit > Test token validation with invalid token")
    void test7() {
        String token = jwtService.generateToken(userDetails);
        String finalToken = token.substring(0, token.length() - 1) + "a";
        assertThrows(SignatureException.class, () -> jwtService.isTokenValid(finalToken, userDetails));
    }

    @Test
    @DisplayName("Unit > Test token validation with invalid user")
    void test8() {
        String token = jwtService.generateToken(userDetails);
        UserDetails invalidUser = org.springframework.security.core.userdetails.User.builder()
                                      .username("invalid")
                                      .password("invalid")
                                      .disabled(true)
                                      .build();
        assertFalse(jwtService.isTokenValid(token, invalidUser));
    }

    @Test
    @DisplayName("Unit > Test token validation with expired token")
    void test9() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 0L);
        String token = jwtService.generateToken(userDetails);
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, userDetails));
    }
}