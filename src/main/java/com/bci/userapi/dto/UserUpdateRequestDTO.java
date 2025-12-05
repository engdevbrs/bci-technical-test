package com.bci.userapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDTO {

    @NotBlank(message = "El nombre es requerido")
    private String name;

    @NotBlank(message = "El correo electrónico es requerido")
    private String email;

    @Valid
    private List<PhoneDTO> phones;
}

