package net.andrecarbajal.sysped.dto;

import net.andrecarbajal.sysped.model.TableStatus;

public record TableResponseDto(
    Integer number,
    TableStatus status
) {}
