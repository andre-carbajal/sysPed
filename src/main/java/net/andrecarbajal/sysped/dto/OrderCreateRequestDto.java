package net.andrecarbajal.sysped.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderCreateRequestDto(
    @NotNull Integer tableNumber,
    @NotEmpty @Valid List<OrderItemDto> items
) {}