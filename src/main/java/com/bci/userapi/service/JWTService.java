package com.bci.userapi.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JWTService implements IJWTService {

    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);
    
    private final SecretKey secretKey;
    private final long expirationTime;

    public JWTService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration}") long expirationTime) {
        try {
            if (secret == null || secret.trim().isEmpty()) {
                throw new IllegalArgumentException("La clave secreta JWT no puede estar vacía");
            }
            
            this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            this.expirationTime = expirationTime;
        } catch (WeakKeyException ex) {
            logger.error("Error: La clave secreta JWT es demasiado débil para el algoritmo HS512. Se requieren al menos 64 caracteres (512 bits)", ex);
            throw new IllegalArgumentException("La clave secreta JWT es demasiado débil. Se requieren al menos 64 caracteres para HS512", ex);
        } catch (Exception ex) {
            logger.error("Error al inicializar JWTService: {}", ex.getMessage(), ex);
            throw new RuntimeException("Error al inicializar JWTService", ex);
        }
    }

    @Override
    public String generateToken(String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("El email no puede estar vacío para generar el token");
            }
            
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + expirationTime);

            return Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(secretKey, SignatureAlgorithm.HS512)
                    .compact();
            
        } catch (WeakKeyException ex) {
            logger.error("Error: La clave secreta es demasiado débil para generar el token - {}", ex.getMessage(), ex);
            throw new RuntimeException("Error de configuración: La clave secreta JWT es demasiado débil", ex);
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Error inesperado al generar token JWT para email: {} - {}", email, ex.getMessage(), ex);
            throw new RuntimeException("Error al generar token JWT", ex);
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return false;
            }
            
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            
            return true;
            
        } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            return false;
        } catch (Exception ex) {
            logger.error("Error inesperado al validar token JWT: {}", ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public String getEmailFromToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new IllegalArgumentException("El token no puede estar vacío");
            }
            
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return claims.getSubject();
            
        } catch (ExpiredJwtException ex) {
            logger.error("Error: Token JWT expirado al intentar extraer email", ex);
            throw new RuntimeException("El token JWT ha expirado", ex);
        } catch (MalformedJwtException ex) {
            logger.error("Error: Token JWT malformado al intentar extraer email", ex);
            throw new RuntimeException("El token JWT está malformado", ex);
        } catch (Exception ex) {
            logger.error("Error al extraer email del token JWT: {}", ex.getMessage(), ex);
            throw new RuntimeException("Error al extraer email del token", ex);
        }
    }
}

