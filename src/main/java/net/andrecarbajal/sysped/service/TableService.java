package net.andrecarbajal.sysped.service;

import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.model.RestaurantTable;
import net.andrecarbajal.sysped.model.TableStatus;
import net.andrecarbajal.sysped.repository.TableRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import net.andrecarbajal.sysped.dto.TableResponseDto;
import net.andrecarbajal.sysped.dto.TableSummaryDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;

    private final SimpMessagingTemplate messagingTemplate;

    public List<TableResponseDto> getOperativeTables() {
        List<RestaurantTable> restaurantTables = tableRepository.findAll();
        return restaurantTables.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TableSummaryDto getTableSummary() {

        long libres = tableRepository.countByStatus(TableStatus.DISPONIBLE);
        long esperando = tableRepository.countByStatus(TableStatus.ESPERANDO_PEDIDO);
        long falta = tableRepository.countByStatus(TableStatus.FALTA_ATENCION);
        long entregado = tableRepository.countByStatus(TableStatus.PEDIDO_ENTREGADO);

        return new TableSummaryDto(libres, esperando, falta, entregado);
    }

    public TableResponseDto updateTableStatus(Integer tableNumber, TableStatus newStatus) {

        RestaurantTable restaurantTable = tableRepository.findByNumber(tableNumber)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada: " + tableNumber));

        restaurantTable.setStatus(newStatus);
        RestaurantTable updatedRestaurantTable = tableRepository.save(restaurantTable);

        TableResponseDto dto = convertToDTO(updatedRestaurantTable);

        messagingTemplate.convertAndSend("/topic/table-status", dto);

        return dto;
    }

    private TableResponseDto convertToDTO(RestaurantTable restaurantTable) {
        return new TableResponseDto(restaurantTable.getNumber(), restaurantTable.getStatus());
    }
}