package net.andrecarbajal.sysped.repository;

import net.andrecarbajal.sysped.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
