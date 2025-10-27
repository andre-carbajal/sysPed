package net.andrecarbajal.sysped.service;

import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.controller.PlateStatusWebSocketController;
import net.andrecarbajal.sysped.dto.PlateDto;
import net.andrecarbajal.sysped.dto.PlateListDto;
import net.andrecarbajal.sysped.dto.PlateStatusDto;
import net.andrecarbajal.sysped.model.Plate;
import net.andrecarbajal.sysped.repository.PlateRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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
        PlateDto fullDto = new PlateDto(updatedPlate.getId(), updatedPlate.getName(), updatedPlate.getDescription(), updatedPlate.getPrice(), updatedPlate.getImageBase64(), updatedPlate.isActive());
        plateStatusWebSocketController.sendPlateUpdate(fullDto);
        return updatedPlate;
    }

    public Plate updatePlate(Long id, BigDecimal price, String imageBase64, boolean active) {
        Plate plate = plateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plato no encontrado"));
        plate.setPrice(price);
        String normalizedImage = (imageBase64 == null || imageBase64.trim().isEmpty()) ? null : imageBase64;
        plate.setImageBase64(normalizedImage);
        plate.setActive(active);
        Plate updatedPlate = plateRepository.save(plate);
        PlateDto fullDto = new PlateDto(updatedPlate.getId(), updatedPlate.getName(), updatedPlate.getDescription(), updatedPlate.getPrice(), updatedPlate.getImageBase64(), updatedPlate.isActive());
        plateStatusWebSocketController.sendPlateUpdate(fullDto);
        return updatedPlate;
    }

    public List<PlateListDto> findAllActivePlates() {
        return plateRepository.findAll().stream()
                .filter(plate -> Boolean.TRUE.equals(plate.isActive()))
                .map(plate -> new PlateListDto(
                        plate.getId(),
                        plate.getName(),
                        plate.getDescription(),
                        plate.getPrice(),
                        plate.getImageBase64(),
                        plate.isActive()
                ))
                .collect(Collectors.toList());
    }
}
