package com.bci.userapi.validator;

import com.bci.userapi.dto.UserUpdateRequestDTO;
import com.bci.userapi.exception.InvalidEmailFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUpdateRequestValidatorTest {

    @Mock
    private EmailValidator emailValidator;

    @InjectMocks
    private UserUpdateRequestValidator validator;

    private UserUpdateRequestDTO userRequest;

    @BeforeEach
    void setUp() {
        userRequest = new UserUpdateRequestDTO();
        userRequest.setName("Juan Rodriguez");
        userRequest.setEmail("juan@rodriguez.cl");
    }

    @Test
    void testValidate_Success() {
        doNothing().when(emailValidator).validate(anyString());

        assertDoesNotThrow(() -> validator.validate(userRequest));
        verify(emailValidator, times(1)).validate(userRequest.getEmail());
    }

    @Test
    void testValidate_NameIsNull() {
        userRequest.setName(null);

        assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(userRequest);
        });

        verify(emailValidator, never()).validate(anyString());
    }

    @Test
    void testValidate_NameIsEmpty() {
        userRequest.setName("");

        assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(userRequest);
        });

        verify(emailValidator, never()).validate(anyString());
    }

    @Test
    void testValidate_NameIsBlank() {
        userRequest.setName("   ");

        assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(userRequest);
        });

        verify(emailValidator, never()).validate(anyString());
    }

    @Test
    void testValidate_InvalidEmail() {
        doThrow(new InvalidEmailFormatException("El formato del correo no es válido"))
                .when(emailValidator).validate(userRequest.getEmail());

        assertThrows(InvalidEmailFormatException.class, () -> {
            validator.validate(userRequest);
        });

        verify(emailValidator, times(1)).validate(userRequest.getEmail());
    }
}

