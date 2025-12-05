package com.bci.userapi.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    @Test
    void testEmailAlreadyExistsException() {
        String message = "El correo ya registrado";
        EmailAlreadyExistsException exception = new EmailAlreadyExistsException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testInvalidEmailFormatException() {
        String message = "El formato del correo no es válido";
        InvalidEmailFormatException exception = new InvalidEmailFormatException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testInvalidPasswordFormatException() {
        String message = "El formato de la contraseña no es válido";
        InvalidPasswordFormatException exception = new InvalidPasswordFormatException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testPasswordMismatchException() {
        String message = "Las contraseñas no coinciden";
        PasswordMismatchException exception = new PasswordMismatchException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testUserNotFoundException() {
        String message = "Usuario no encontrado";
        UserNotFoundException exception = new UserNotFoundException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }
}

