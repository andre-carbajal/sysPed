package net.andrecarbajal.sysped.repository;

import net.andrecarbajal.sysped.model.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
}
