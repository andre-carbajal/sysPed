package net.andrecarbajal.sysped.service;

import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.model.Table;
import net.andrecarbajal.sysped.model.TableStatus;
import net.andrecarbajal.sysped.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.andrecarbajal.sysped.dto.TableResponseDto;
import net.andrecarbajal.sysped.dto.TableStatusUpdateDto;
import net.andrecarbajal.sysped.dto.TableSummaryDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TableService {

    @Autowired
    private TableRepository tableRepository;

    // Método para el grid de mesas
    public List<TableResponseDto> getOperativeTables() {
        List<Table> tables = tableRepository.findAllByStatusNot(TableStatus.FUERA_DE_SERVICIO);
        return tables.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Método para que el mozo actualice el estado
    public TableResponseDto updateTableStatus(Integer tableNumber, TableStatusUpdateDto updateDTO) {
        Table table = tableRepository.findByNumber(tableNumber)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada: " + tableNumber));

        table.setStatus(updateDTO.newStatus());
        Table updatedTable = tableRepository.save(table);

        return convertToDTO(updatedTable);
    }

    // Helper privado para mapear
    private TableResponseDto convertToDTO(Table table) {
        return new TableResponseDto(table.getNumber(), table.getStatus());
    }
}
