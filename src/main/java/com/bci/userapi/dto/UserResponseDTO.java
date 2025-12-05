package com.bci.userapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private UUID id;
    private LocalDateTime created;
    private LocalDateTime modified;
    
    @JsonProperty("last_login")
    private LocalDateTime lastLogin;
    
    private String token;
    private Boolean isactive;
}

