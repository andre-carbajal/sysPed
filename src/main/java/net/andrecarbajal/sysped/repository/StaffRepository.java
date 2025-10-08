package net.andrecarbajal.sysped.repository;

import net.andrecarbajal.sysped.model.Staff;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends ListCrudRepository<Staff, String> {
    Optional<Staff> findByDni(String dni);
}