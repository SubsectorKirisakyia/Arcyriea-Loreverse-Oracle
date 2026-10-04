package com.arcyriea_loreverse.oracle_cloud.crud.dtos.always;

import com.arcyriea_loreverse.oracle_cloud.crud.entities.always.Role;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public class AccountDTOs {
    // Request DTO: What the user sends to you
    public record AccountRequest(
            @NotBlank String username,
            @NotBlank String password,
            Set<Role> roles
    ) {}

    // Response DTO: What you send back to the user (NO passwordHash here!)
    public record AccountResponse(
            Long id,
            String username,
            boolean enabled,
            Set<Role> roles
    ) {}
}
