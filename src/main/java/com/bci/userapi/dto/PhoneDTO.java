package com.bci.userapi.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneDTO {

    @NotBlank(message = "El número de teléfono es requerido")
    private String number;

    @NotBlank(message = "El código de ciudad es requerido")
    private String citycode;

    @NotBlank(message = "El código de país es requerido")
    @JsonProperty("countrycode")
    private String countrycode;
}

