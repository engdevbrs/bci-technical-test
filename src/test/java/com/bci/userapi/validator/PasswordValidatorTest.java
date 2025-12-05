package com.bci.userapi.validator;

import com.bci.userapi.exception.InvalidPasswordFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    private PasswordValidator passwordValidator;

    @BeforeEach
    void setUp() {
        passwordValidator = new PasswordValidator("^[a-zA-Z0-9]{8,}$");
    }

    @Test
    void testValidate_ValidPassword() {
        assertDoesNotThrow(() -> passwordValidator.validate("hunter2"));
        assertDoesNotThrow(() -> passwordValidator.validate("password123"));
        assertDoesNotThrow(() -> passwordValidator.validate("ABCD1234"));
    }

    @Test
    void testValidate_InvalidPassword() {
        assertThrows(InvalidPasswordFormatException.class, () -> {
            passwordValidator.validate("short");
        });

        assertThrows(InvalidPasswordFormatException.class, () -> {
            passwordValidator.validate("with-special!");
        });

        assertThrows(InvalidPasswordFormatException.class, () -> {
            passwordValidator.validate(null);
        });
    }
}

