package net.andrecarbajal.sysped.service;

import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.model.Plate;
import net.andrecarbajal.sysped.repository.PlateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlateService {
    private final PlateRepository plateRepository;

    public List<Plate> findAllPlates() {
        return this.plateRepository.findAll();
    }
}
