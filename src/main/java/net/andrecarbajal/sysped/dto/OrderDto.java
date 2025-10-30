package net.andrecarbajal.sysped.dto;

import net.andrecarbajal.sysped.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {
    public Long id;
    public Integer tableNumber;
    public LocalDateTime dateAndTimeOrder;
    public OrderStatus status;
    public BigDecimal totalPrice;
    public List<OrderItemResponseDto> items;

    public OrderDto() {}

    public OrderDto(Long id, Integer tableNumber, LocalDateTime dateAndTimeOrder, OrderStatus status, BigDecimal totalPrice, List<OrderItemResponseDto> items) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.dateAndTimeOrder = dateAndTimeOrder;
        this.status = status;
        this.totalPrice = totalPrice;
        this.items = items;
    }
}

