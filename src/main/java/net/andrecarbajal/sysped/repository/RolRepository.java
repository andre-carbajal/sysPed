package net.andrecarbajal.sysped.repository;

import net.andrecarbajal.sysped.model.Rol;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends ListCrudRepository<Rol, Long> {
    Optional<Rol> findByName(String name);
}
