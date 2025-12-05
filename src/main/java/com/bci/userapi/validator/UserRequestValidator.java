package com.bci.userapi.validator;

import com.bci.userapi.dto.UserRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class UserRequestValidator {

    private final EmailValidator emailValidator;
    private final PasswordValidator passwordValidator;
    private final EmailDuplicationValidator emailDuplicationValidator;

    public UserRequestValidator(EmailValidator emailValidator,
                                PasswordValidator passwordValidator,
                                EmailDuplicationValidator emailDuplicationValidator) {
        this.emailValidator = emailValidator;
        this.passwordValidator = passwordValidator;
        this.emailDuplicationValidator = emailDuplicationValidator;
    }

    public void validate(UserRequestDTO userRequest) {
        emailValidator.validate(userRequest.getEmail());
        passwordValidator.validate(userRequest.getPassword());
        emailDuplicationValidator.validate(userRequest.getEmail());
    }
}

