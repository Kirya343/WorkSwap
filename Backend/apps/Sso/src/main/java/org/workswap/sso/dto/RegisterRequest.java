package org.workswap.sso.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest (
    @NotBlank
    @Pattern(
        regexp = "^[A-Za-zА-Яа-яЁё0-9]+([\\s\\-'][A-Za-zА-Яа-яЁё0-9]+)*$",
        message = "Имя может содержать только буквы, пробелы, дефис или апостроф"
    )
    String name,

    @Email
    @NotBlank
    String email,

    @Size(min = 8)
    String password
) {}