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
import com.bci.userapi.validator.EmailValidator;
import com.bci.userapi.validator.PasswordValidator;
import com.bci.userapi.validator.UserRequestValidator;
import com.bci.userapi.validator.UserUpdateRequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserRequestValidator validator;
    private final UserFactory userFactory;
    private final IUserMapper userMapper;
    private final IJWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailValidator emailValidator;
    private final PasswordValidator passwordValidator;
    private final EmailDuplicationValidator emailDuplicationValidator;
    private final UserUpdateRequestValidator updateValidator;
    private final ChangePasswordRequestValidator changePasswordValidator;

    public UserService(UserRepository userRepository,
                       UserRequestValidator validator,
                       UserFactory userFactory,
                       IUserMapper userMapper,
                       IJWTService jwtService,
                       PasswordEncoder passwordEncoder,
                       EmailValidator emailValidator,
                       PasswordValidator passwordValidator,
                       EmailDuplicationValidator emailDuplicationValidator,
                       UserUpdateRequestValidator updateValidator,
                       ChangePasswordRequestValidator changePasswordValidator) {
        this.userRepository = userRepository;
        this.validator = validator;
        this.userFactory = userFactory;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.emailValidator = emailValidator;
        this.passwordValidator = passwordValidator;
        this.emailDuplicationValidator = emailDuplicationValidator;
        this.updateValidator = updateValidator;
        this.changePasswordValidator = changePasswordValidator;
    }

    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequest) {
        try {
            validator.validate(userRequest);

            String token = jwtService.generateToken(userRequest.getEmail());
            User user = userFactory.createUser(userRequest, token);
            user = userRepository.save(user);

            return userMapper.toResponseDTO(user);

        } catch (EmailAlreadyExistsException | InvalidEmailFormatException | InvalidPasswordFormatException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Error al crear usuario para email: {} - Error: {}",
                    userRequest.getEmail(), ex.getMessage(), ex);
            throw new RuntimeException("Error al crear usuario", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailResponseDTO getUserById(UUID id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
            return userMapper.toDetailResponseDTO(user);
        } catch (UserNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Error al obtener usuario con ID: {} - Error: {}", id, ex.getMessage(), ex);
            throw new RuntimeException("Error al obtener usuario", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserListResponseDTO> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return users.stream()
                    .map(userMapper::toListResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            logger.error("Error al obtener lista de usuarios - Error: {}", ex.getMessage(), ex);
            throw new RuntimeException("Error al obtener lista de usuarios", ex);
        }
    }

    @Override
    @Transactional
    public UserUpdateResponseDTO updateUser(UUID id, UserUpdateRequestDTO userRequest) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

            updateValidator.validate(userRequest);

            if (!user.getEmail().equals(userRequest.getEmail())) {
                emailDuplicationValidator.validate(userRequest.getEmail());
            }

            user.setName(userRequest.getName());
            user.setEmail(userRequest.getEmail());
            user.setModified(LocalDateTime.now());

            if (userRequest.getPhones() != null && !userRequest.getPhones().isEmpty()) {
                user.getPhones().clear();
                for (PhoneDTO phoneDTO : userRequest.getPhones()) {
                    Phone phone = userMapper.toPhoneEntity(phoneDTO, user);
                    user.getPhones().add(phone);
                }
            }

            user = userRepository.save(user);
            return userMapper.toUpdateResponseDTO(user);

        } catch (UserNotFoundException | IllegalArgumentException | EmailAlreadyExistsException | InvalidEmailFormatException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Error al actualizar usuario ID: {} - Error: {}", id, ex.getMessage(), ex);
            throw new RuntimeException("Error al actualizar usuario", ex);
        }
    }

    @Override
    @Transactional
    public ChangePasswordResponseDTO changePassword(UUID id, ChangePasswordRequestDTO request) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

            changePasswordValidator.validate(request);

            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setModified(LocalDateTime.now());
            userRepository.save(user);

            return new ChangePasswordResponseDTO("Contraseña cambiada con éxito");

        } catch (UserNotFoundException | InvalidPasswordFormatException | PasswordMismatchException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Error al cambiar contraseña ID: {} - Error: {}", id, ex.getMessage(), ex);
            throw new RuntimeException("Error al cambiar contraseña", ex);
        }
    }

    @Override
    @Transactional
    public DeleteUserResponseDTO deleteUser(UUID id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
            userRepository.delete(user);
            return new DeleteUserResponseDTO("Usuario eliminado con éxito");
        } catch (UserNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Error al eliminar usuario ID: {} - Error: {}", id, ex.getMessage(), ex);
            throw new RuntimeException("Error al eliminar usuario", ex);
        }
    }
}

