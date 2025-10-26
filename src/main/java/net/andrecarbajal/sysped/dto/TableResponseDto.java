package net.andrecarbajal.sysped.dto;

import net.andrecarbajal.sysped.model.TableStatus;

public record TableResponseDto(
        Long id,
        Integer number,
        TableStatus status
) {}
