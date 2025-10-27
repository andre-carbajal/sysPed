package net.andrecarbajal.sysped.dto;

import net.andrecarbajal.sysped.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderCreateResponseDto(
    Long orderId,
    Integer tableNumber,
    LocalDateTime dateAndTimeOrder,
    OrderStatus status,
    BigDecimal totalPrice,
    List<OrderItemResponseDto> items
) {}