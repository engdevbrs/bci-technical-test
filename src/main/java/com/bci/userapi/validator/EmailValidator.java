package com.bci.userapi.validator;

import com.bci.userapi.exception.InvalidEmailFormatException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class EmailValidator implements IValidator<String> {

    private final Pattern emailPattern;

    public EmailValidator(@Value("${validation.email.regex}") String emailRegex) {
        this.emailPattern = Pattern.compile(emailRegex);
    }

    @Override
    public void validate(String email) {
        if (email == null || !emailPattern.matcher(email).matches()) {
            throw new InvalidEmailFormatException("El formato del correo no es válido");
        }
    }
}

