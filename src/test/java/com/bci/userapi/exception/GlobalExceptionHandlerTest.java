package com.bci.userapi.exception;

import com.bci.userapi.dto.ErrorResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleEmailAlreadyExists() {
        EmailAlreadyExistsException ex = new EmailAlreadyExistsException("El correo ya registrado");
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleEmailAlreadyExists(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("El correo ya registrado", response.getBody().getMensaje());
    }

    @Test
    void testHandleInvalidEmailFormat() {
        InvalidEmailFormatException ex = new InvalidEmailFormatException("El formato del correo no es válido");
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleInvalidEmailFormat(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("El formato del correo no es válido", response.getBody().getMensaje());
    }

    @Test
    void testHandleInvalidPasswordFormat() {
        InvalidPasswordFormatException ex = new InvalidPasswordFormatException("El formato de la contraseña no es válido");
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleInvalidPasswordFormat(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("El formato de la contraseña no es válido", response.getBody().getMensaje());
    }

    @Test
    void testHandlePasswordMismatch() {
        PasswordMismatchException ex = new PasswordMismatchException("Las contraseñas no coinciden");
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handlePasswordMismatch(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Las contraseñas no coinciden", response.getBody().getMensaje());
    }

    @Test
    void testHandleIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("El nombre es requerido");
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("El nombre es requerido", response.getBody().getMensaje());
    }

    @Test
    void testHandleUserNotFound() {
        UserNotFoundException ex = new UserNotFoundException("Usuario no encontrado");
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleUserNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Usuario no encontrado", response.getBody().getMensaje());
    }


    @Test
    void testHandleHttpMediaTypeNotSupported() {
        HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException("Content type '' not supported");
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleHttpMediaTypeNotSupported(ex);

        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("El tipo de contenido no es válido. Se requiere application/json", response.getBody().getMensaje());
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new RuntimeException("Error inesperado");
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error interno del servidor", response.getBody().getMensaje());
    }
}

