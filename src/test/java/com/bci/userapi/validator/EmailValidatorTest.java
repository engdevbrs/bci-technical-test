package com.bci.userapi.validator;

import com.bci.userapi.exception.InvalidEmailFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailValidatorTest {

    private EmailValidator emailValidator;

    @BeforeEach
    void setUp() {
        emailValidator = new EmailValidator("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.cl$");
    }

    @Test
    void testValidate_ValidEmail() {
        assertDoesNotThrow(() -> emailValidator.validate("juan@rodriguez.cl"));
        assertDoesNotThrow(() -> emailValidator.validate("test@example.cl"));
        assertDoesNotThrow(() -> emailValidator.validate("user.name@domain.cl"));
    }

    @Test
    void testValidate_InvalidEmail() {
        assertThrows(InvalidEmailFormatException.class, () -> {
            emailValidator.validate("invalid-email");
        });

        assertThrows(InvalidEmailFormatException.class, () -> {
            emailValidator.validate("test@example.com");
        });

        assertThrows(InvalidEmailFormatException.class, () -> {
            emailValidator.validate(null);
        });
    }
}

