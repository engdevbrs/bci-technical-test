package com.bci.userapi.validator;

import com.bci.userapi.dto.UserUpdateRequestDTO;
import com.bci.userapi.exception.InvalidEmailFormatException;
import org.springframework.stereotype.Component;

@Component
public class UserUpdateRequestValidator {
    
    private final EmailValidator emailValidator;

    public UserUpdateRequestValidator(EmailValidator emailValidator) {
        this.emailValidator = emailValidator;
    }

    public void validate(UserUpdateRequestDTO userRequest) {
        if (userRequest.getName() == null || userRequest.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es requerido");
        }
        emailValidator.validate(userRequest.getEmail());
    }
}

