package com.bci.userapi.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JWTServiceTest {

    private JWTService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JWTService(
                "mySecretKeyForJWTTokenGenerationThatShouldBeAtLeast512BitsLongForHS512AlgorithmSecurity",
                86400000L
        );
    }

    @Test
    void testConstructor_WithNullSecret() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            new JWTService(null, 86400000L);
        });
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
        assertTrue(ex.getMessage().contains("Error al inicializar JWTService"));
    }

    @Test
    void testConstructor_WithEmptySecret() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            new JWTService("", 86400000L);
        });
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
        assertTrue(ex.getMessage().contains("Error al inicializar JWTService"));
    }

    @Test
    void testConstructor_WithWhitespaceSecret() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            new JWTService("   ", 86400000L);
        });
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
        assertTrue(ex.getMessage().contains("Error al inicializar JWTService"));
    }

    @Test
    void testConstructor_WithWeakKey() {
        assertThrows(IllegalArgumentException.class, () -> {
            new JWTService("short", 86400000L);
        });
    }

    @Test
    void testGenerateToken() {
        String email = "test@example.cl";
        String token = jwtService.generateToken(email);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testGenerateToken_WithNullEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            jwtService.generateToken(null);
        });
    }

    @Test
    void testGenerateToken_WithEmptyEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            jwtService.generateToken("");
        });
    }

    @Test
    void testGenerateToken_WithWhitespaceEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            jwtService.generateToken("   ");
        });
    }

    @Test
    void testValidateToken_ValidToken() {
        String email = "test@example.cl";
        String token = jwtService.generateToken(email);

        boolean isValid = jwtService.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidToken() {
        String invalidToken = "invalid.token.here";

        boolean isValid = jwtService.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    void testValidateToken_WithNullToken() {
        boolean isValid = jwtService.validateToken(null);
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_WithEmptyToken() {
        boolean isValid = jwtService.validateToken("");
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_WithWhitespaceToken() {
        boolean isValid = jwtService.validateToken("   ");
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_WithExpiredToken() throws Exception {
        String shortSecret = "mySecretKeyForJWTTokenGenerationThatShouldBeAtLeast512BitsLongForHS512AlgorithmSecurity";
        SecretKey key = Keys.hmacShaKeyFor(shortSecret.getBytes(StandardCharsets.UTF_8));
        
        String expiredToken = Jwts.builder()
                .setSubject("test@example.cl")
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        boolean isValid = jwtService.validateToken(expiredToken);
        assertFalse(isValid);
    }

    @Test
    void testGetEmailFromToken() {
        String email = "test@example.cl";
        String token = jwtService.generateToken(email);

        String extractedEmail = jwtService.getEmailFromToken(token);

        assertEquals(email, extractedEmail);
    }

    @Test
    void testGetEmailFromToken_WithNullToken() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            jwtService.getEmailFromToken(null);
        });
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
        assertTrue(ex.getMessage().contains("Error al extraer email del token"));
    }

    @Test
    void testGetEmailFromToken_WithEmptyToken() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            jwtService.getEmailFromToken("");
        });
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
        assertTrue(ex.getMessage().contains("Error al extraer email del token"));
    }

    @Test
    void testGetEmailFromToken_WithWhitespaceToken() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            jwtService.getEmailFromToken("   ");
        });
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
        assertTrue(ex.getMessage().contains("Error al extraer email del token"));
    }

    @Test
    void testGetEmailFromToken_WithExpiredToken() throws Exception {
        String shortSecret = "mySecretKeyForJWTTokenGenerationThatShouldBeAtLeast512BitsLongForHS512AlgorithmSecurity";
        SecretKey key = Keys.hmacShaKeyFor(shortSecret.getBytes(StandardCharsets.UTF_8));
        
        String expiredToken = Jwts.builder()
                .setSubject("test@example.cl")
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        assertThrows(RuntimeException.class, () -> {
            jwtService.getEmailFromToken(expiredToken);
        });
    }

    @Test
    void testGetEmailFromToken_WithMalformedToken() {
        assertThrows(RuntimeException.class, () -> {
            jwtService.getEmailFromToken("malformed.token.here");
        });
    }
}

