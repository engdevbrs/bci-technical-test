package com.bci.userapi.validator;

import com.bci.userapi.exception.InvalidPasswordFormatException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordValidator implements IValidator<String> {

    private final Pattern passwordPattern;

    public PasswordValidator(@Value("${validation.password.regex}") String passwordRegex) {
        this.passwordPattern = Pattern.compile(passwordRegex);
    }

    @Override
    public void validate(String password) {
        if (password == null || !passwordPattern.matcher(password).matches()) {
            throw new InvalidPasswordFormatException("El formato de la contraseña no es válido");
        }
    }
}

