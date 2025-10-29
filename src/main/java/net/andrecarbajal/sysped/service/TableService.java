package net.andrecarbajal.sysped.service;

import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.dto.TableResponseDto;
import net.andrecarbajal.sysped.dto.TableSummaryDto;
import net.andrecarbajal.sysped.model.RestaurantTable;
import net.andrecarbajal.sysped.model.TableStatus;
import net.andrecarbajal.sysped.repository.TableRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
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

        TableStatus currentStatus = restaurantTable.getStatus();

        Set<TableStatus> allowed = allowedFromStatus(currentStatus);

        if (!allowed.contains(newStatus)) {
            throw new IllegalStateException("Operación no permitida: " + currentStatus + " -> " + newStatus);
        }

        restaurantTable.setStatus(newStatus);
        RestaurantTable updatedRestaurantTable = tableRepository.save(restaurantTable);

        TableResponseDto dto = convertToDTO(updatedRestaurantTable);

        messagingTemplate.convertAndSend("/topic/table-status", dto);

        return dto;
    }

    private Set<TableStatus> allowedFromStatus(TableStatus currentStatus) {
        return switch (currentStatus) {
            case DISPONIBLE -> Set.of(TableStatus.FUERA_DE_SERVICIO, TableStatus.ESPERANDO_PEDIDO);
            case ESPERANDO_PEDIDO ->
                    Set.of(TableStatus.FALTA_ATENCION, TableStatus.PEDIDO_ENTREGADO, TableStatus.DISPONIBLE);
            case FALTA_ATENCION -> Set.of(TableStatus.DISPONIBLE, TableStatus.PEDIDO_ENTREGADO);
            case PEDIDO_ENTREGADO ->
                    Set.of(TableStatus.DISPONIBLE, TableStatus.ESPERANDO_PEDIDO, TableStatus.FALTA_ATENCION);
            case FUERA_DE_SERVICIO -> Set.of(TableStatus.DISPONIBLE);
        };
    }

    public Set<TableStatus> getAllowedStatuses(Integer tableNumber) {
        RestaurantTable restaurantTable = tableRepository.findByNumber(tableNumber)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada: " + tableNumber));
        return allowedFromStatus(restaurantTable.getStatus());
    }

    private TableResponseDto convertToDTO(RestaurantTable restaurantTable) {
        return new TableResponseDto(restaurantTable.getNumber(), restaurantTable.getStatus());
    }
}