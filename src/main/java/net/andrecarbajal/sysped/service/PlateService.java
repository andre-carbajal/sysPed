package net.andrecarbajal.sysped.service;

import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.controller.PlateStatusWebSocketController;
import net.andrecarbajal.sysped.dto.PlateStatusDto;
import net.andrecarbajal.sysped.model.Plate;
import net.andrecarbajal.sysped.repository.PlateRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlateService {
    private final PlateRepository plateRepository;
    private final PlateStatusWebSocketController plateStatusWebSocketController;

    public Plate setPlateActive(Long plateId, boolean active) {
        Plate plate = plateRepository.findById(plateId)
                .orElseThrow(() -> new IllegalArgumentException("Plato no encontrado"));
        plate.setActive(active);
        Plate updatedPlate = plateRepository.save(plate);
        PlateStatusDto dto = new PlateStatusDto(updatedPlate.getId(), updatedPlate.isActive());
        plateStatusWebSocketController.sendPlateStatusUpdate(dto);
        return updatedPlate;
    }
}
