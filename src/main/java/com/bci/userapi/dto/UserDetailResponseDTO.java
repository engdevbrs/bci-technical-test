package com.bci.userapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponseDTO {

    private UUID id;
    private String name;
    private String email;
    private List<PhoneDTO> phones;
    
    @JsonProperty("is_active")
    private Boolean isActive;
}

