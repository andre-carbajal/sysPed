package net.andrecarbajal.sysped.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.dto.StaffCreateRequestDto;
import net.andrecarbajal.sysped.dto.StaffEditRequestDto;
import net.andrecarbajal.sysped.model.Rol;
import net.andrecarbajal.sysped.model.Staff;
import net.andrecarbajal.sysped.exception.EntityNotFound;
import net.andrecarbajal.sysped.repository.StaffRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StaffService {
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createStaff(StaffCreateRequestDto dto, Rol rol) {
        if (existStaffByDni(dto.dni())) {
            throw new IllegalArgumentException("Ya existe un usuario con el DNI " + dto.dni());
        }
        Staff staff = new Staff();
        staff.setDni(dto.dni());
        staff.setName(dto.name());
        staff.setPassword(passwordEncoder.encode(dto.password()));
        staff.setRol(rol);
        staffRepository.save(staff);
    }

    @Transactional
    public void deleteStaffByDni(String dni) {
        staffRepository.deleteById(dni);
    }

    public boolean existStaffByDni(String dni) {
        return staffRepository.existsById(dni);
    }

    @Transactional
    public void updateStaff(StaffEditRequestDto dto, Rol rol) throws EntityNotFound {
        Staff staff = staffRepository.findByDni(dto.dni())
                .orElseThrow(() -> new EntityNotFound("Staff no encontrado"));
        staff.setName(dto.name());
        staff.setRol(rol);
        if (dto.password() != null && !dto.password().isBlank()) {
            staff.setPassword(passwordEncoder.encode(dto.password()));
        }
        staffRepository.save(staff);
    }

    public List<Staff> findAllStaff() {
        return staffRepository.findAll();
    }

    public Optional<Staff> findStaffByDni(String dni) {
        return staffRepository.findByDni(dni);
    }
}
