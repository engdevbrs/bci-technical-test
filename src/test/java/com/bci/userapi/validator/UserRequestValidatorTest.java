package com.bci.userapi.validator;

import com.bci.userapi.dto.UserRequestDTO;
import com.bci.userapi.exception.EmailAlreadyExistsException;
import com.bci.userapi.exception.InvalidEmailFormatException;
import com.bci.userapi.exception.InvalidPasswordFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRequestValidatorTest {

    @Mock
    private EmailValidator emailValidator;

    @Mock
    private PasswordValidator passwordValidator;

    @Mock
    private EmailDuplicationValidator emailDuplicationValidator;

    @InjectMocks
    private UserRequestValidator validator;

    private UserRequestDTO userRequest;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequestDTO();
        userRequest.setName("Juan Rodriguez");
        userRequest.setEmail("juan@rodriguez.cl");
        userRequest.setPassword("hunter123");
    }

    @Test
    void testValidate_Success() {
        doNothing().when(emailValidator).validate(anyString());
        doNothing().when(passwordValidator).validate(anyString());
        doNothing().when(emailDuplicationValidator).validate(anyString());

        assertDoesNotThrow(() -> validator.validate(userRequest));

        verify(emailValidator, times(1)).validate(userRequest.getEmail());
        verify(passwordValidator, times(1)).validate(userRequest.getPassword());
        verify(emailDuplicationValidator, times(1)).validate(userRequest.getEmail());
    }

    @Test
    void testValidate_InvalidEmail() {
        doThrow(new InvalidEmailFormatException("El formato del correo no es válido"))
                .when(emailValidator).validate(anyString());

        assertThrows(InvalidEmailFormatException.class, () -> {
            validator.validate(userRequest);
        });

        verify(passwordValidator, never()).validate(anyString());
        verify(emailDuplicationValidator, never()).validate(anyString());
    }

    @Test
    void testValidate_InvalidPassword() {
        doNothing().when(emailValidator).validate(anyString());
        doThrow(new InvalidPasswordFormatException("El formato de la contraseña no es válido"))
                .when(passwordValidator).validate(anyString());

        assertThrows(InvalidPasswordFormatException.class, () -> {
            validator.validate(userRequest);
        });

        verify(emailDuplicationValidator, never()).validate(anyString());
    }

    @Test
    void testValidate_EmailAlreadyExists() {
        doNothing().when(emailValidator).validate(anyString());
        doNothing().when(passwordValidator).validate(anyString());
        doThrow(new EmailAlreadyExistsException("El correo ya registrado"))
                .when(emailDuplicationValidator).validate(anyString());

        assertThrows(EmailAlreadyExistsException.class, () -> {
            validator.validate(userRequest);
        });
    }
}

