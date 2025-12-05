package com.bci.userapi.service;

import com.bci.userapi.dto.PhoneDTO;
import com.bci.userapi.dto.UserRequestDTO;
import com.bci.userapi.dto.UserResponseDTO;
import com.bci.userapi.entity.User;
import com.bci.userapi.exception.EmailAlreadyExistsException;
import com.bci.userapi.exception.InvalidEmailFormatException;
import com.bci.userapi.exception.InvalidPasswordFormatException;
import com.bci.userapi.factory.UserFactory;
import com.bci.userapi.mapper.IUserMapper;
import com.bci.userapi.repository.UserRepository;
import com.bci.userapi.validator.UserRequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRequestValidator validator;

    @Mock
    private UserFactory userFactory;

    @Mock
    private IUserMapper userMapper;

    @Mock
    private IJWTService jwtService;

    @InjectMocks
    private UserService userService;

    private UserRequestDTO userRequest;
    private User user;
    private UserResponseDTO userResponse;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequestDTO();
        userRequest.setName("Juan Rodriguez");
        userRequest.setEmail("juan@rodriguez.cl");
        userRequest.setPassword("hunter2");

        PhoneDTO phone = new PhoneDTO();
        phone.setNumber("1234567");
        phone.setCitycode("1");
        phone.setCountrycode("57");
        userRequest.setPhones(new ArrayList<>());
        userRequest.getPhones().add(phone);

        user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Juan Rodriguez");
        user.setEmail("juan@rodriguez.cl");

        userResponse = new UserResponseDTO();
        userResponse.setId(user.getId());
        userResponse.setName("Juan Rodriguez");
        userResponse.setEmail("juan@rodriguez.cl");
    }

    @Test
    void testCreateUser_Success() {
        doNothing().when(validator).validate(any(UserRequestDTO.class));
        when(jwtService.generateToken(anyString())).thenReturn("test-token");
        when(userFactory.createUser(any(UserRequestDTO.class), anyString())).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(userResponse);

        UserResponseDTO result = userService.createUser(userRequest);

        assertNotNull(result);
        assertEquals(userRequest.getName(), result.getName());
        assertEquals(userRequest.getEmail(), result.getEmail());
        verify(validator, times(1)).validate(userRequest);
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void testCreateUser_EmailAlreadyExists() {
        doThrow(new EmailAlreadyExistsException("El correo ya registrado"))
                .when(validator).validate(any(UserRequestDTO.class));

        assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.createUser(userRequest);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUser_InvalidEmailFormat() {
        doThrow(new InvalidEmailFormatException("El formato del correo no es válido"))
                .when(validator).validate(any(UserRequestDTO.class));

        assertThrows(InvalidEmailFormatException.class, () -> {
            userService.createUser(userRequest);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUser_InvalidPasswordFormat() {
        doThrow(new InvalidPasswordFormatException("El formato de la contraseña no es válido"))
                .when(validator).validate(any(UserRequestDTO.class));

        assertThrows(InvalidPasswordFormatException.class, () -> {
            userService.createUser(userRequest);
        });

        verify(userRepository, never()).save(any(User.class));
    }
}

