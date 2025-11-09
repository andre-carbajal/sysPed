package net.andrecarbajal.sysped.dto;

import java.math.BigDecimal;

public record OrderItemResponseDto(
    Long plateId,
    PlateDto plate,
    int quantity,
    BigDecimal priceUnit,
    BigDecimal totalPrice,
    String notes
) {}