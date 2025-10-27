package net.andrecarbajal.sysped.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemDto(
    @NotNull Long plateId,
    @Min(1) int quantity,
    String notes
) {}