package net.andrecarbajal.sysped.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.dto.StaffCreateRequestDto;
import net.andrecarbajal.sysped.dto.StaffEditRequestDto;
import net.andrecarbajal.sysped.model.Rol;
import net.andrecarbajal.sysped.model.Staff;
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
        Optional<Staff> existingStaff = staffRepository.findByDni(dto.dni());

        Staff staff;
        if (existingStaff.isPresent()) {
            staff = existingStaff.get();
            if (staff.getActive()) {
                throw new IllegalArgumentException("Ya existe un usuario activo con el DNI " + dto.dni());
            }
            staff.setActive(true);
            staff.setName(dto.name());
            staff.setPassword(passwordEncoder.encode(dto.password()));
            staff.setRol(rol);
        } else {
            staff = new Staff();
            staff.setDni(dto.dni());
            staff.setName(dto.name());
            staff.setPassword(passwordEncoder.encode(dto.password()));
            staff.setRol(rol);
            staff.setActive(true);
        }
        staffRepository.save(staff);
    }

    @Transactional
    public void deleteStaffByDni(String dni) {
        Staff staff = staffRepository.findByDni(dni)
                .orElseThrow(() -> new IllegalArgumentException("Staff no encontrado"));
        staff.setActive(false);
        staffRepository.save(staff);
    }

    public boolean existStaffByDni(String dni) {
        return staffRepository.findByDniAndActiveTrue(dni).isPresent();
    }

    @Transactional
    public void updateStaff(StaffEditRequestDto dto, Rol rol) {
        Staff staff = staffRepository.findByDni(dto.dni())
                .orElseThrow(() -> new IllegalArgumentException("Staff no encontrado"));
        staff.setName(dto.name());
        staff.setRol(rol);
        if (dto.password() != null && !dto.password().isBlank()) {
            staff.setPassword(passwordEncoder.encode(dto.password()));
        }
        staffRepository.save(staff);
    }

    public List<Staff> findAllStaff() {
        return staffRepository.findByActiveTrue();
    }

    public Optional<Staff> findStaffByDni(String dni) {
        return staffRepository.findByDni(dni);
    }
}
