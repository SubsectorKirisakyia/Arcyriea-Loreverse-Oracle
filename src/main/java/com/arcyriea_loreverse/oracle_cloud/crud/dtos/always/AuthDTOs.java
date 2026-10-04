package com.arcyriea_loreverse.oracle_cloud.crud.dtos.always;

import com.arcyriea_loreverse.oracle_cloud.crud.entities.always.Role;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public class AuthDTOs {
    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {}

    public record AuthResponse(
            String token,
            String username,
            Set<Role> roles
    ) {}

}
