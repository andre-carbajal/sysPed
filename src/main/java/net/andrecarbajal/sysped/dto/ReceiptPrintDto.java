package net.andrecarbajal.sysped.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptPrintDto {
    private Long receiptId;
    private Long orderId;
    private String receiptType;
    private String customerName;
    private String dni;
    private String ruc;
    private BigDecimal discount;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    
    private Integer tableNumber;
    private LocalDateTime orderDate;
    private String waiterName;
    private List<OrderItemDto> items;
}