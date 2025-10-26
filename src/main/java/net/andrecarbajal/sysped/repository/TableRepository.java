package net.andrecarbajal.sysped.repository;
import net.andrecarbajal.sysped.model.Table;
import net.andrecarbajal.sysped.model.TableStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableRepository extends JpaRepository<Table, Long> {

    List<Table> findAllByStatusNot(TableStatus status);

    long countByStatus(TableStatus status);

    Optional<Table> findByNumber(Integer number);
}
