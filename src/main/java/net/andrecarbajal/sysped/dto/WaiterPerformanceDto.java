package net.andrecarbajal.sysped.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaiterPerformanceDto {
    private String staffDni;
    private String staffName;
    private Long tablesServed;
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private BigDecimal averagePerTable;
    private Double performanceScore;
}