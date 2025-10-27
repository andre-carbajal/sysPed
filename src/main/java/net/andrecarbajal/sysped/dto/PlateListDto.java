package net.andrecarbajal.sysped.dto;

import java.math.BigDecimal;

public record PlateListDto(
    Long id,
    String name,
    String description,
    BigDecimal price,
    String imageBase64,
    Boolean active
) {}