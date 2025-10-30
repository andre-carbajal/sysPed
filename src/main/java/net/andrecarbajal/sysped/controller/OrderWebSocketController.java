package net.andrecarbajal.sysped.controller;

import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.dto.OrderDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class OrderWebSocketController {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendOrderUpdate(OrderDto orderDto) {
        messagingTemplate.convertAndSend("/topic/order-updates", orderDto);
    }
}

