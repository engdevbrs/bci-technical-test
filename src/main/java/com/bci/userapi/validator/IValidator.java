package com.bci.userapi.validator;

public interface IValidator<T> {
    void validate(T object) throws RuntimeException;
}

