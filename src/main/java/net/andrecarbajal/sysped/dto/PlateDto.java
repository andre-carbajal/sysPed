package net.andrecarbajal.sysped.dto;

import java.math.BigDecimal;

public record PlateDto(
    Long id,
    String name,
    String description,
    BigDecimal price,
    String imageBase64,
    Boolean active
) {
}