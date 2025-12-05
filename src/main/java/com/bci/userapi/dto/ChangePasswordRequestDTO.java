package com.bci.userapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDTO {

    @NotBlank(message = "La contraseña es requerida")
    private String password;

    @NotBlank(message = "La confirmación de contraseña es requerida")
    private String confirmPassword;
}

