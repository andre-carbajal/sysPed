package net.andrecarbajal.sysped.dto;

import java.math.BigDecimal;

public record OrderItemResponseDto(
    Long plateId,
    String plateName,
    int quantity,
    BigDecimal priceUnit,
    BigDecimal totalPrice,
    String notes
) {}