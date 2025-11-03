package net.andrecarbajal.sysped.controller;

import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.dto.TableStatusUpdateDto;
import net.andrecarbajal.sysped.model.TableStatus;
import net.andrecarbajal.sysped.service.TableService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class TableStatusWebSocketController {
    private final TableService tableService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/mesas/update-status")
    public void updateTableStatusWS(TableStatusUpdateDto dto) {
        try {
            TableStatus newStatus = TableStatus.valueOf(dto.status());
            tableService.updateTableStatus(dto.tableNumber(), newStatus);
            messagingTemplate.convertAndSend("/topic/table-status", dto);
        } catch (IllegalArgumentException e) {
            messagingTemplate.convertAndSend("/topic/table-errors", "Estado no válido: " + dto.status());
        } catch (Exception e) {
            messagingTemplate.convertAndSend("/topic/table-errors", "ERROR: " + e.getMessage());
        }
    }
}