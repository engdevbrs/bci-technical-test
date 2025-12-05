package com.bci.userapi.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Test
    void testPasswordEncoderBean() {
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder.matches("test123", passwordEncoder.encode("test123")));
        assertFalse(passwordEncoder.matches("wrong", passwordEncoder.encode("test123")));
    }

    @Test
    void testCorsConfigurationSourceBean() {
        assertNotNull(corsConfigurationSource);
    }

    @Test
    void testSecurityConfigExists() {
        assertNotNull(securityConfig);
    }
}

