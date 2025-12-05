package com.bci.userapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    @NotBlank(message = "El nombre es requerido")
    private String name;

    @NotBlank(message = "El correo es requerido")
    @Email(message = "El formato del correo no es válido")
    private String email;

    @NotBlank(message = "La contraseña es requerida")
    private String password;

    @NotEmpty(message = "Debe incluir al menos un teléfono")
    @Valid
    private List<PhoneDTO> phones;
}

