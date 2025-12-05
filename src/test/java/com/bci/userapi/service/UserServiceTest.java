package com.bci.userapi.service;

import com.bci.userapi.dto.ChangePasswordRequestDTO;
import com.bci.userapi.dto.ChangePasswordResponseDTO;
import com.bci.userapi.dto.DeleteUserResponseDTO;
import com.bci.userapi.dto.PhoneDTO;
import com.bci.userapi.dto.UserDetailResponseDTO;
import com.bci.userapi.dto.UserListResponseDTO;
import com.bci.userapi.dto.UserRequestDTO;
import com.bci.userapi.dto.UserResponseDTO;
import com.bci.userapi.dto.UserUpdateRequestDTO;
import com.bci.userapi.dto.UserUpdateResponseDTO;
import com.bci.userapi.entity.Phone;
import com.bci.userapi.entity.User;
import com.bci.userapi.exception.EmailAlreadyExistsException;
import com.bci.userapi.exception.InvalidEmailFormatException;
import com.bci.userapi.exception.InvalidPasswordFormatException;
import com.bci.userapi.exception.PasswordMismatchException;
import com.bci.userapi.exception.UserNotFoundException;
import com.bci.userapi.factory.UserFactory;
import com.bci.userapi.mapper.IUserMapper;
import com.bci.userapi.repository.UserRepository;
import com.bci.userapi.validator.ChangePasswordRequestValidator;
import com.bci.userapi.validator.EmailDuplicationValidator;
import com.bci.userapi.validator.UserRequestValidator;
import com.bci.userapi.validator.UserUpdateRequestValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailDuplicationValidator emailDuplicationValidator;

    @Mock
    private UserUpdateRequestValidator updateValidator;

    @Mock
    private ChangePasswordRequestValidator changePasswordValidator;

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
        userResponse.setToken("test-token");
        userResponse.setIsactive(true);
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
        assertEquals(user.getId(), result.getId());
        assertNotNull(result.getToken());
        verify(validator, times(1)).validate(userRequest);
        verify(userRepository, times(1)).save(any(User.class));
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

    @Test
    void testGetUserById_Success() {
        UUID userId = UUID.randomUUID();
        UserDetailResponseDTO detailResponse = new UserDetailResponseDTO();
        detailResponse.setId(userId);
        detailResponse.setName("Juan Rodriguez");
        detailResponse.setEmail("juan@rodriguez.cl");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDetailResponseDTO(user)).thenReturn(detailResponse);

        UserDetailResponseDTO result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserById_UserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(userId);
        });

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, never()).toDetailResponseDTO(any(User.class));
    }

    @Test
    void testGetAllUsers_Success() {
        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setName("Maria Garcia");
        user2.setEmail("maria@garcia.cl");

        UserListResponseDTO listResponse1 = new UserListResponseDTO();
        listResponse1.setId(user.getId());
        listResponse1.setName("Juan Rodriguez");

        UserListResponseDTO listResponse2 = new UserListResponseDTO();
        listResponse2.setId(user2.getId());
        listResponse2.setName("Maria Garcia");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));
        when(userMapper.toListResponseDTO(user)).thenReturn(listResponse1);
        when(userMapper.toListResponseDTO(user2)).thenReturn(listResponse2);

        List<UserListResponseDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetAllUsers_EmptyList() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        List<UserListResponseDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testUpdateUser_Success() {
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setEmail("juan@rodriguez.cl");

        UserUpdateRequestDTO updateRequest = new UserUpdateRequestDTO();
        updateRequest.setName("Juan Rodriguez Actualizado");
        updateRequest.setEmail("juan.actualizado@rodriguez.cl");

        UserUpdateResponseDTO updateResponse = new UserUpdateResponseDTO();
        updateResponse.setId(userId);
        updateResponse.setName("Juan Rodriguez Actualizado");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(updateValidator).validate(updateRequest);
        doNothing().when(emailDuplicationValidator).validate(anyString());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUpdateResponseDTO(user)).thenReturn(updateResponse);

        UserUpdateResponseDTO result = userService.updateUser(userId, updateRequest);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUser_UserNotFound() {
        UUID userId = UUID.randomUUID();
        UserUpdateRequestDTO updateRequest = new UserUpdateRequestDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(userId, updateRequest);
        });

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUser_EmailDuplicated() {
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setEmail("juan@rodriguez.cl");

        UserUpdateRequestDTO updateRequest = new UserUpdateRequestDTO();
        updateRequest.setName("Juan Rodriguez");
        updateRequest.setEmail("nuevo@email.cl");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(updateValidator).validate(updateRequest);
        doThrow(new EmailAlreadyExistsException("El correo ya registrado"))
                .when(emailDuplicationValidator).validate("nuevo@email.cl");

        assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.updateUser(userId, updateRequest);
        });

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testChangePassword_Success() {
        UUID userId = UUID.randomUUID();
        user.setId(userId);

        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO();
        request.setPassword("newpassword123");
        request.setConfirmPassword("newpassword123");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(changePasswordValidator).validate(request);
        when(passwordEncoder.encode("newpassword123")).thenReturn("encoded-password");
        when(userRepository.save(user)).thenReturn(user);

        ChangePasswordResponseDTO result = userService.changePassword(userId, request);

        assertNotNull(result);
        assertEquals("Contraseña cambiada con éxito", result.getMensaje());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testChangePassword_UserNotFound() {
        UUID userId = UUID.randomUUID();
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.changePassword(userId, request);
        });

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testChangePassword_PasswordMismatch() {
        UUID userId = UUID.randomUUID();
        user.setId(userId);

        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO();
        request.setPassword("newpassword123");
        request.setConfirmPassword("different123");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doThrow(new PasswordMismatchException("Las contraseñas no coinciden"))
                .when(changePasswordValidator).validate(request);

        assertThrows(PasswordMismatchException.class, () -> {
            userService.changePassword(userId, request);
        });

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        UUID userId = UUID.randomUUID();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        DeleteUserResponseDTO result = userService.deleteUser(userId);

        assertNotNull(result);
        assertEquals("Usuario eliminado con éxito", result.getMensaje());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(userId);
        });

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void testCreateUser_GenericException() {
        doThrow(new RuntimeException("Database error")).when(validator).validate(any(UserRequestDTO.class));

        assertThrows(RuntimeException.class, () -> {
            userService.createUser(userRequest);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserById_GenericException() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            userService.getUserById(userId);
        });
    }

    @Test
    void testGetAllUsers_GenericException() {
        when(userRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            userService.getAllUsers();
        });
    }

    @Test
    void testUpdateUser_GenericException() {
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setEmail("juan@rodriguez.cl");
        UserUpdateRequestDTO updateRequest = new UserUpdateRequestDTO();
        updateRequest.setName("Juan Rodriguez");
        updateRequest.setEmail("juan.nuevo@rodriguez.cl");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(updateValidator).validate(updateRequest);
        doNothing().when(emailDuplicationValidator).validate(anyString());
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            userService.updateUser(userId, updateRequest);
        });
    }

    @Test
    void testChangePassword_GenericException() {
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO();
        request.setPassword("newpassword123");
        request.setConfirmPassword("newpassword123");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(changePasswordValidator).validate(request);
        when(passwordEncoder.encode(anyString())).thenThrow(new RuntimeException("Encoding error"));

        assertThrows(RuntimeException.class, () -> {
            userService.changePassword(userId, request);
        });
    }

    @Test
    void testDeleteUser_GenericException() {
        UUID userId = UUID.randomUUID();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doThrow(new RuntimeException("Database error")).when(userRepository).delete(user);

        assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(userId);
        });
    }

    @Test
    void testUpdateUser_WithPhones() {
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setEmail("juan@rodriguez.cl");

        UserUpdateRequestDTO updateRequest = new UserUpdateRequestDTO();
        updateRequest.setName("Juan Rodriguez Actualizado");
        updateRequest.setEmail("juan.actualizado@rodriguez.cl");

        PhoneDTO phoneDTO = new PhoneDTO();
        phoneDTO.setNumber("1111111");
        phoneDTO.setCitycode("2");
        phoneDTO.setCountrycode("56");
        updateRequest.setPhones(new ArrayList<>());
        updateRequest.getPhones().add(phoneDTO);

        Phone phone = new Phone();
        phone.setNumber("1111111");
        phone.setCitycode("2");
        phone.setCountrycode("56");

        UserUpdateResponseDTO updateResponse = new UserUpdateResponseDTO();
        updateResponse.setId(userId);
        updateResponse.setName("Juan Rodriguez Actualizado");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(updateValidator).validate(updateRequest);
        doNothing().when(emailDuplicationValidator).validate(anyString());
        when(userMapper.toPhoneEntity(any(PhoneDTO.class), any(User.class))).thenReturn(phone);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUpdateResponseDTO(user)).thenReturn(updateResponse);

        UserUpdateResponseDTO result = userService.updateUser(userId, updateRequest);

        assertNotNull(result);
        verify(userMapper, times(1)).toPhoneEntity(any(PhoneDTO.class), any(User.class));
    }

    @Test
    void testUpdateUser_SameEmail() {
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setEmail("juan@rodriguez.cl");

        UserUpdateRequestDTO updateRequest = new UserUpdateRequestDTO();
        updateRequest.setName("Juan Rodriguez Actualizado");
        updateRequest.setEmail("juan@rodriguez.cl");

        UserUpdateResponseDTO updateResponse = new UserUpdateResponseDTO();
        updateResponse.setId(userId);
        updateResponse.setName("Juan Rodriguez Actualizado");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(updateValidator).validate(updateRequest);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUpdateResponseDTO(user)).thenReturn(updateResponse);

        UserUpdateResponseDTO result = userService.updateUser(userId, updateRequest);

        assertNotNull(result);
        verify(emailDuplicationValidator, never()).validate(anyString());
    }

    @Test
    void testUpdateUser_WithNullPhones() {
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setEmail("juan@rodriguez.cl");

        UserUpdateRequestDTO updateRequest = new UserUpdateRequestDTO();
        updateRequest.setName("Juan Rodriguez Actualizado");
        updateRequest.setEmail("juan.nuevo@rodriguez.cl");
        updateRequest.setPhones(null);

        UserUpdateResponseDTO updateResponse = new UserUpdateResponseDTO();
        updateResponse.setId(userId);
        updateResponse.setName("Juan Rodriguez Actualizado");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(updateValidator).validate(updateRequest);
        doNothing().when(emailDuplicationValidator).validate(anyString());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUpdateResponseDTO(user)).thenReturn(updateResponse);

        UserUpdateResponseDTO result = userService.updateUser(userId, updateRequest);

        assertNotNull(result);
        verify(userMapper, never()).toPhoneEntity(any(PhoneDTO.class), any(User.class));
    }

    @Test
    void testUpdateUser_WithEmptyPhones() {
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setEmail("juan@rodriguez.cl");

        UserUpdateRequestDTO updateRequest = new UserUpdateRequestDTO();
        updateRequest.setName("Juan Rodriguez Actualizado");
        updateRequest.setEmail("juan.nuevo@rodriguez.cl");
        updateRequest.setPhones(new ArrayList<>());

        UserUpdateResponseDTO updateResponse = new UserUpdateResponseDTO();
        updateResponse.setId(userId);
        updateResponse.setName("Juan Rodriguez Actualizado");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(updateValidator).validate(updateRequest);
        doNothing().when(emailDuplicationValidator).validate(anyString());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUpdateResponseDTO(user)).thenReturn(updateResponse);

        UserUpdateResponseDTO result = userService.updateUser(userId, updateRequest);

        assertNotNull(result);
        verify(userMapper, never()).toPhoneEntity(any(PhoneDTO.class), any(User.class));
    }
}

