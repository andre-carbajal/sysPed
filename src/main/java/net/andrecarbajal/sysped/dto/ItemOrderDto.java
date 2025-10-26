package net.andrecarbajal.sysped.dto;

public record ItemOrderDto(
    Long plateId,
    int quantity,
    String notes
) {
}
