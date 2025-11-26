package net.andrecarbajal.sysped.repository;

import net.andrecarbajal.sysped.model.Order;
import net.andrecarbajal.sysped.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("""
            SELECT new map(
                CAST(o.dateandtimeOrder AS LocalDate) as date,
                SUM(r.total) as totalRevenue,
                COUNT(o.id) as orderCount
            )
            FROM Order o
            LEFT JOIN o.receipt r
            WHERE o.dateandtimeOrder BETWEEN :startDate AND :endDate
            AND o.status = 'PAGADO'
            GROUP BY CAST(o.dateandtimeOrder AS LocalDate)
            ORDER BY CAST(o.dateandtimeOrder AS LocalDate) DESC
            """)
    List<Object> findRevenueByDay(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
            SELECT new map(
                FUNCTION('DATE_FORMAT', o.dateandtimeOrder, '%Y-%m') as period,
                SUM(r.total) as totalRevenue,
                COUNT(o.id) as orderCount
            )
            FROM Order o
            LEFT JOIN o.receipt r
            WHERE o.dateandtimeOrder BETWEEN :startDate AND :endDate
            AND o.status = 'PAGADO'
            GROUP BY FUNCTION('DATE_FORMAT', o.dateandtimeOrder, '%Y-%m')
            ORDER BY FUNCTION('DATE_FORMAT', o.dateandtimeOrder, '%Y-%m') DESC
            """)
    List<Object> findRevenueByMonth(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
            SELECT new map(
                CAST(o.dateandtimeOrder AS LocalDate) as date,
                SUM(od.quantity) as totalQuantity
            )
            FROM Order o
            JOIN o.details od
            WHERE o.dateandtimeOrder BETWEEN :startDate AND :endDate
            AND o.status = 'PAGADO'
            GROUP BY CAST(o.dateandtimeOrder AS LocalDate)
            ORDER BY SUM(od.quantity) DESC
            """)
    List<Object> findDaysWithMostPlatesSold(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
            SELECT new map(
                s.dni as staffDni,
                s.name as staffName,
                COUNT(DISTINCT o.restaurantTable.id) as tablesServed,
                COUNT(o.id) as totalOrders,
                COALESCE(SUM(r.total), 0) as totalRevenue
            )
            FROM Order o
            JOIN o.staff s
            LEFT JOIN o.receipt r
            WHERE o.dateandtimeOrder BETWEEN :startDate AND :endDate
            AND o.status = 'PAGADO'
            AND s.rol.name = 'MOZO'
            GROUP BY s.dni, s.name
            ORDER BY COUNT(DISTINCT o.restaurantTable.id) DESC
            """)
    List<Object> findWaiterPerformance(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
            SELECT COALESCE(SUM(r.total), 0)
            FROM Order o
            LEFT JOIN o.receipt r
            WHERE o.dateandtimeOrder BETWEEN :startDate AND :endDate
            AND o.status = 'PAGADO'
            """)
    BigDecimal getTotalRevenueInPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
            SELECT COUNT(o.id)
            FROM Order o
            WHERE o.dateandtimeOrder BETWEEN :startDate AND :endDate
            AND o.status = 'PAGADO'
            """)
    Long getOrderCountInPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    Optional<Order> findByRestaurantTable_NumberAndStatusIn(Integer tableNumber, Set<OrderStatus> statuses);
}