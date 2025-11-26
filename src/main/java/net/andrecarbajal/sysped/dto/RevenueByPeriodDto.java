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
public class RevenueByPeriodDto {
    private LocalDate date;
    private String period;
    private BigDecimal totalRevenue;
    private Long orderCount;
    private BigDecimal averageTicket;
}