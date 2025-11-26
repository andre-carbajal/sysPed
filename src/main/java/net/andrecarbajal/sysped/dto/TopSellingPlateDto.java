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
public class TopSellingPlateDto {
    private Long plateId;
    private String plateName;
    private String categoryName;
    private Long quantitySold;
    private BigDecimal totalRevenue;
    private BigDecimal averagePrice;
    private Double percentageOfTotal;
}