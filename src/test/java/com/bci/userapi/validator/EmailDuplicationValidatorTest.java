package com.bci.userapi.validator;

import com.bci.userapi.entity.User;
import com.bci.userapi.exception.EmailAlreadyExistsException;
import com.bci.userapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailDuplicationValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmailDuplicationValidator validator;

    private String email;
    private User existingUser;

    @BeforeEach
    void setUp() {
        email = "juan@rodriguez.cl";
        existingUser = new User();
        existingUser.setId(UUID.randomUUID());
        existingUser.setEmail(email);
    }

    @Test
    void testValidate_EmailNotExists() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> validator.validate(email));
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testValidate_EmailExists() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        assertThrows(EmailAlreadyExistsException.class, () -> {
            validator.validate(email);
        });

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testValidate_GenericException() {
        when(userRepository.findByEmail(email)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            validator.validate(email);
        });

        verify(userRepository, times(1)).findByEmail(email);
    }
}

