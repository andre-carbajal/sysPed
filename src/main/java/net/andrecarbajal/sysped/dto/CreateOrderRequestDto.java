package net.andrecarbajal.sysped.dto;

import java.util.List;

public record CreateOrderRequestDto(
        List<ItemOrderDto> items
){
}
