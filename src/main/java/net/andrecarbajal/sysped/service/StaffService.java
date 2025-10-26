package net.andrecarbajal.sysped.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.dto.StaffCreateRequestDto;
import net.andrecarbajal.sysped.dto.StaffEditRequestDto;
import net.andrecarbajal.sysped.model.Rol;
import net.andrecarbajal.sysped.model.Staff;
import net.andrecarbajal.sysped.model.StaffAudit;
import net.andrecarbajal.sysped.repository.StaffRepository;
import net.andrecarbajal.sysped.repository.StaffAuditRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StaffService {
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    private final StaffAuditRepository staffAuditRepository;

    private String currentUsername() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return "SYSTEM";
            }
            return auth.getName();
        } catch (Exception e) {
            return "SYSTEM";
        }
    }

    @Transactional
    public void createStaff(StaffCreateRequestDto dto, Rol rol) {
        Optional<Staff> existingStaff = staffRepository.findByDni(dto.dni());

        Staff staff;
        boolean isNew = false;
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
            isNew = true;
        }
        Staff saved = staffRepository.save(staff);

        // Auditing
        StaffAudit audit = new StaffAudit();
        audit.setDni(saved.getDni());
        audit.setName(saved.getName());
        audit.setRolName(saved.getRol() != null ? saved.getRol().getName() : null);
        audit.setWhenEvent(OffsetDateTime.now());
        audit.setPerformedBy(currentUsername());
        audit.setAction(isNew ? "INSERT" : "REACTIVATE");
        try {
            staffAuditRepository.save(audit);
        } catch (Exception e) {
            System.err.println("Failed to save staff audit: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteStaffByDni(String dni) {
        Staff staff = staffRepository.findByDni(dni)
                .orElseThrow(() -> new IllegalArgumentException("Staff no encontrado"));
        staff.setActive(false);
        Staff saved = staffRepository.save(staff);

        StaffAudit audit = new StaffAudit();
        audit.setDni(saved.getDni());
        audit.setName(saved.getName());
        audit.setRolName(saved.getRol() != null ? saved.getRol().getName() : null);
        audit.setWhenEvent(OffsetDateTime.now());
        audit.setPerformedBy(currentUsername());
        audit.setAction("DELETE");
        try {
            staffAuditRepository.save(audit);
        } catch (Exception e) {
            System.err.println("Failed to save staff audit: " + e.getMessage());
        }
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
        Staff saved = staffRepository.save(staff);

        StaffAudit audit = new StaffAudit();
        audit.setDni(saved.getDni());
        audit.setName(saved.getName());
        audit.setRolName(saved.getRol() != null ? saved.getRol().getName() : null);
        audit.setWhenEvent(OffsetDateTime.now());
        audit.setPerformedBy(currentUsername());
        audit.setAction("UPDATE");
        try {
            staffAuditRepository.save(audit);
        } catch (Exception e) {
            System.err.println("Failed to save staff audit: " + e.getMessage());
        }
    }

    public List<Staff> findAllStaff() {
        return staffRepository.findByActiveTrue();
    }

    public Optional<Staff> findStaffByDni(String dni) {
        return staffRepository.findByDni(dni);
    }
}
