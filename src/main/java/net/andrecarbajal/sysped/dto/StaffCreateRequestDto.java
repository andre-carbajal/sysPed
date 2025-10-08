package net.andrecarbajal.sysped.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record StaffCreateRequestDto(
        @NotEmpty
        @Pattern(regexp = "\\d{8}", message = "El DNI debe tener exactamente 8 dígitos")
        String dni,
        @NotEmpty String password,
        @NotEmpty String name,
        @NotEmpty String rolName
) {
}
