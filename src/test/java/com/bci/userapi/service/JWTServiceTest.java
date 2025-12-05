package com.bci.userapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JWTServiceTest {

    private JWTService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JWTService(
                "mySecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLong",
                86400000L
        );
    }

    @Test
    void testGenerateToken() {
        String email = "test@example.cl";
        String token = jwtService.generateToken(email);

        assertNotNull(token);
        assertFalse(token.isEmpty());
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
    void testGetEmailFromToken() {
        String email = "test@example.cl";
        String token = jwtService.generateToken(email);

        String extractedEmail = jwtService.getEmailFromToken(token);

        assertEquals(email, extractedEmail);
    }
}

