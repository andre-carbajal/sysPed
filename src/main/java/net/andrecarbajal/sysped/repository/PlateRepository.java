package net.andrecarbajal.sysped.repository;

import net.andrecarbajal.sysped.model.Plate;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlateRepository extends ListCrudRepository<Plate, Long> {
}
