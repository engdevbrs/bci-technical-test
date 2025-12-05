package com.bci.userapi.validator;

import com.bci.userapi.dto.ChangePasswordRequestDTO;
import com.bci.userapi.exception.InvalidPasswordFormatException;
import com.bci.userapi.exception.PasswordMismatchException;
import org.springframework.stereotype.Component;

@Component
public class ChangePasswordRequestValidator {
    
    private final PasswordValidator passwordValidator;

    public ChangePasswordRequestValidator(PasswordValidator passwordValidator) {
        this.passwordValidator = passwordValidator;
    }

    public void validate(ChangePasswordRequestDTO request) {
        passwordValidator.validate(request.getPassword());
        
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Las contraseñas no coinciden");
        }
    }
}

