package net.andrecarbajal.sysped.repository;

import net.andrecarbajal.sysped.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Optional<Receipt> findByOrderId(Long orderId);

    @Query("SELECT r FROM Receipt r " +
            "JOIN FETCH r.order o " +
            "JOIN FETCH o.restaurantTable t " +
            "JOIN FETCH o.staff s " +
            "JOIN FETCH o.details d " +
            "JOIN FETCH d.plate p " +
            "WHERE o.id = :orderId")
    Optional<Receipt> findByOrderIdWithDetails(@Param("orderId") Long orderId);

    @Query("SELECT DISTINCT r FROM Receipt r " +
            "JOIN FETCH r.order o " +
            "JOIN FETCH o.restaurantTable t " +
            "JOIN FETCH o.staff s " +
            "ORDER BY r.id DESC")
    List<Receipt> findAllWithDetails();
}

