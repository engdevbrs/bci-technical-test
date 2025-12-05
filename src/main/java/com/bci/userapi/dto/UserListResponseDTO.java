package com.bci.userapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponseDTO {

    private UUID id;
    private String name;
    private String email;
    private List<PhoneDTO> phones;
    private LocalDateTime created;
    private LocalDateTime modified;
    
    @JsonProperty("is_active")
    private Boolean isActive;
    
    @JsonProperty("last_login")
    private LocalDateTime lastLogin;
}

