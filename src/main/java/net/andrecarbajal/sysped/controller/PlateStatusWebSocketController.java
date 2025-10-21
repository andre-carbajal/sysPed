package net.andrecarbajal.sysped.controller;

import net.andrecarbajal.sysped.dto.PlateDto;
import net.andrecarbajal.sysped.dto.PlateStatusDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class PlateStatusWebSocketController {
    private final SimpMessagingTemplate messagingTemplate;

    public PlateStatusWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendPlateStatusUpdate(PlateStatusDto plateStatusDto) {
        messagingTemplate.convertAndSend("/topic/plate-status", plateStatusDto);
    }

    public void sendPlateUpdate(PlateDto plateDto) {
        messagingTemplate.convertAndSend("/topic/plate-updates", plateDto);
    }
}
