package net.andrecarbajal.sysped.repository;

import net.andrecarbajal.sysped.model.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
    @Query("""
            SELECT new map(
                p.id as plateId,
                p.name as plateName,
                c.name as categoryName,
                SUM(od.quantity) as quantitySold,
                SUM(od.priceUnit * od.quantity) as totalRevenue,
                AVG(od.priceUnit) as averagePrice
            )
            FROM OrderDetails od
            JOIN od.plate p
            LEFT JOIN p.category c
            JOIN od.order o
            WHERE o.dateandtimeOrder BETWEEN :startDate AND :endDate
            AND o.status = net.andrecarbajal.sysped.model.OrderStatus.PAGADO
            GROUP BY p.id, p.name, c.name
            ORDER BY SUM(od.quantity) DESC
            """)
    List<Map<String, Object>> findTopSellingPlatesByQuantity(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
            SELECT new map(
                p.id as plateId,
                p.name as plateName,
                c.name as categoryName,
                SUM(od.quantity) as quantitySold,
                SUM(od.priceUnit * od.quantity) as totalRevenue,
                AVG(od.priceUnit) as averagePrice
            )
            FROM OrderDetails od
            JOIN od.plate p
            LEFT JOIN p.category c
            JOIN od.order o
            WHERE o.dateandtimeOrder BETWEEN :startDate AND :endDate
            AND o.status = net.andrecarbajal.sysped.model.OrderStatus.PAGADO
            GROUP BY p.id, p.name, c.name
            ORDER BY SUM(od.priceUnit * od.quantity) DESC
            """)
    List<Map<String, Object>> findTopSellingPlatesByRevenue(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
            SELECT SUM(od.quantity)
            FROM OrderDetails od
            JOIN od.order o
            WHERE o.dateandtimeOrder BETWEEN :startDate AND :endDate
            AND o.status = net.andrecarbajal.sysped.model.OrderStatus.PAGADO
            """)
    Long getTotalPlatesSoldInPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}