package net.andrecarbajal.sysped.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsSummaryDto {
    private BigDecimal todayRevenue;
    private BigDecimal monthRevenue;
    private Long todayOrders;
    private Long monthOrders;
    private BigDecimal averageTicket;
    private String topSellingPlate;
    private LocalDate peakSalesDate;
    private BigDecimal peakSalesAmount;
}