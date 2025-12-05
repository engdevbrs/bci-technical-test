package com.bci.userapi.service;

public interface IJWTService {
    String generateToken(String email);
    boolean validateToken(String token);
    String getEmailFromToken(String token);
}

