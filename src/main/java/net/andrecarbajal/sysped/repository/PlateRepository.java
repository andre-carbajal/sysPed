package net.andrecarbajal.sysped.repository;

import net.andrecarbajal.sysped.model.Plate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlateRepository extends JpaRepository<Plate, Long> {
}
