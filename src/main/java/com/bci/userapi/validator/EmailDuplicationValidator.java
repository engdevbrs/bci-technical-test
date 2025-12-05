package com.bci.userapi.validator;

import com.bci.userapi.exception.EmailAlreadyExistsException;
import com.bci.userapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailDuplicationValidator implements IValidator<String> {

    private static final Logger logger = LoggerFactory.getLogger(EmailDuplicationValidator.class);
    private final UserRepository userRepository;

    public EmailDuplicationValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void validate(String email) {
        try {
            boolean exists = userRepository.findByEmail(email).isPresent();
            if (exists) {
                throw new EmailAlreadyExistsException("El correo ya registrado");
            }
        } catch (EmailAlreadyExistsException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Error al validar duplicación de email: {} - {}", email, ex.getMessage(), ex);
            throw new RuntimeException("Error al validar duplicación de email", ex);
        }
    }
}

