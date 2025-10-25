package net.andrecarbajal.sysped.dto;

import net.andrecarbajal.sysped.model.TableStatus;

public record TableStatusUpdateDto(
    TableStatus newStatus
) {
}
