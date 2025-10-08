package net.andrecarbajal.sysped.service;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.model.Rol;
import net.andrecarbajal.sysped.exception.EntityNotFound;
import net.andrecarbajal.sysped.repository.RolRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolService {
    private final RolRepository rolRepository;

    public List<Rol> findAllRol() {
        return this.rolRepository.findAll();
    }

    public Rol findRolByName(@NotEmpty String name) throws EntityNotFound {
        return this.rolRepository.findByName(name).orElseThrow(() -> new EntityNotFound("Rol no encontrado"));
    }
}
