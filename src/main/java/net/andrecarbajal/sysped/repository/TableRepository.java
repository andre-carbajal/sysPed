package net.andrecarbajal.sysped.repository;

import net.andrecarbajal.sysped.model.RestaurantTable;
import net.andrecarbajal.sysped.model.TableStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TableRepository extends JpaRepository<RestaurantTable, Long> {
    long countByStatus(TableStatus status);

    Optional<RestaurantTable> findByNumber(Integer number);
}
