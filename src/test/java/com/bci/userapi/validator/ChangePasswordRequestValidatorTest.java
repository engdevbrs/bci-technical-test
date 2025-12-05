package com.bci.userapi.validator;

import com.bci.userapi.dto.ChangePasswordRequestDTO;
import com.bci.userapi.exception.InvalidPasswordFormatException;
import com.bci.userapi.exception.PasswordMismatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangePasswordRequestValidatorTest {

    @Mock
    private PasswordValidator passwordValidator;

    @InjectMocks
    private ChangePasswordRequestValidator validator;

    private ChangePasswordRequestDTO request;

    @BeforeEach
    void setUp() {
        request = new ChangePasswordRequestDTO();
        request.setPassword("newpassword123");
        request.setConfirmPassword("newpassword123");
    }

    @Test
    void testValidate_Success() {
        doNothing().when(passwordValidator).validate(anyString());

        assertDoesNotThrow(() -> validator.validate(request));
        verify(passwordValidator, times(1)).validate(request.getPassword());
    }

    @Test
    void testValidate_PasswordMismatch() {
        request.setConfirmPassword("different123");
        doNothing().when(passwordValidator).validate(anyString());

        assertThrows(PasswordMismatchException.class, () -> {
            validator.validate(request);
        });

        verify(passwordValidator, times(1)).validate(request.getPassword());
    }

    @Test
    void testValidate_InvalidPasswordFormat() {
        doThrow(new InvalidPasswordFormatException("El formato de la contraseña no es válido"))
                .when(passwordValidator).validate(request.getPassword());

        assertThrows(InvalidPasswordFormatException.class, () -> {
            validator.validate(request);
        });

        verify(passwordValidator, times(1)).validate(request.getPassword());
    }
}

