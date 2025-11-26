package net.andrecarbajal.sysped.service;

import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.controller.OrderWebSocketController;
import net.andrecarbajal.sysped.dto.OrderItemDto;
import net.andrecarbajal.sysped.dto.PlateDto;
import net.andrecarbajal.sysped.dto.ReceiptCreateRequestDto;
import net.andrecarbajal.sysped.dto.ReceiptPrintDto;
import net.andrecarbajal.sysped.dto.ReceiptResponseDto;
import net.andrecarbajal.sysped.mapper.OrderMapper;
import net.andrecarbajal.sysped.model.Order;
import net.andrecarbajal.sysped.model.OrderDetails;
import net.andrecarbajal.sysped.model.OrderStatus;
import net.andrecarbajal.sysped.model.Receipt;
import net.andrecarbajal.sysped.model.TableStatus;
import net.andrecarbajal.sysped.repository.OrderRepository;
import net.andrecarbajal.sysped.repository.ReceiptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReceiptService {
    private static final BigDecimal IGV_DIVISOR = new BigDecimal("1.18");

    private final ReceiptRepository receiptRepository;
    private final OrderRepository orderRepository;
    private final OrderWebSocketController orderWebSocketController;
    private final TableService tableService;

    @Transactional
    public ReceiptResponseDto createReceipt(Long orderId, ReceiptCreateRequestDto request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + orderId));

        if (order.getStatus() != OrderStatus.LISTO) {
            throw new IllegalStateException("Solo se pueden crear recibos para pedidos en estado LISTO. Estado actual: " + order.getStatus());
        }

        if (order.getReceipt() != null) {
            throw new IllegalStateException("El pedido ya tiene un recibo asociado");
        }

        BigDecimal discount = request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO;

        if (discount.compareTo(order.getPriceTotal()) > 0) {
            throw new IllegalArgumentException("El descuento no puede ser mayor que el total del pedido");
        }

        BigDecimal totalConDescuento = order.getPriceTotal().subtract(discount);
        BigDecimal subtotal = totalConDescuento.divide(IGV_DIVISOR, 2, RoundingMode.HALF_UP);
        BigDecimal igv = totalConDescuento.subtract(subtotal);

        Receipt receipt = new Receipt();
        receipt.setOrder(order);
        receipt.setDiscount(discount);
        receipt.setSubtotal(subtotal);
        receipt.setIgv(igv);
        receipt.setTotal(totalConDescuento);

        if ("FACTURA".equals(request.getReceiptType())) {
            receipt.setRuc(request.getRuc());
            receipt.setCustomerName(request.getCustomerName());
            receipt.setDni(null);
        } else {
            receipt.setDni(request.getDni());
            receipt.setCustomerName(request.getCustomerName());
            receipt.setRuc(null);
        }

        receipt = receiptRepository.save(receipt);

        order.setStatus(OrderStatus.PAGADO);
        order.setReceipt(receipt);
        orderRepository.save(order);

        orderWebSocketController.sendOrderUpdate(OrderMapper.toDto(order));

        tableService.updateTableStatus(order.getRestaurantTable().getNumber(), TableStatus.DISPONIBLE);

        return toResponseDto(receipt);
    }

    @Transactional(readOnly = true)
    public Optional<ReceiptResponseDto> getReceiptByOrderId(Long orderId) {
        return receiptRepository.findByOrderId(orderId)
                .map(this::toResponseDto);
    }

    @Transactional(readOnly = true)
    public Optional<ReceiptPrintDto> getReceiptForPrint(Long orderId) {
        return receiptRepository.findByOrderIdWithDetails(orderId)
                .map(this::toPrintDto);
    }

    @Transactional(readOnly = true)
    public List<ReceiptPrintDto> getAllReceipts() {
        return receiptRepository.findAllWithDetails().stream()
                .map(this::toPrintDto)
                .collect(Collectors.toList());
    }

    private ReceiptResponseDto toResponseDto(Receipt receipt) {
        String receiptType = (receipt.getRuc() != null && !receipt.getRuc().isBlank()) ? "FACTURA" : "BOLETA";

        return ReceiptResponseDto.builder()
                .receiptId(receipt.getId())
                .orderId(receipt.getOrder().getId())
                .receiptType(receiptType)
                .customerName(receipt.getCustomerName())
                .dni(receipt.getDni())
                .ruc(receipt.getRuc())
                .discount(receipt.getDiscount())
                .subtotal(receipt.getSubtotal())
                .igv(receipt.getIgv())
                .total(receipt.getTotal())
                .build();
    }

    private ReceiptPrintDto toPrintDto(Receipt receipt) {
        Order order = receipt.getOrder();
        String receiptType = (receipt.getRuc() != null && !receipt.getRuc().isBlank()) ? "FACTURA" : "BOLETA";

        List<OrderItemDto> items = order.getDetails().stream()
                .map(this::toOrderItemDto)
                .collect(Collectors.toList());

        return ReceiptPrintDto.builder()
                .receiptId(receipt.getId())
                .orderId(order.getId())
                .receiptType(receiptType)
                .customerName(receipt.getCustomerName())
                .dni(receipt.getDni())
                .ruc(receipt.getRuc())
                .discount(receipt.getDiscount())
                .subtotal(receipt.getSubtotal())
                .igv(receipt.getIgv())
                .total(receipt.getTotal())
                .tableNumber(order.getRestaurantTable().getNumber())
                .orderDate(order.getDateandtimeOrder())
                .waiterName(order.getStaff().getName())
                .items(items)
                .build();
    }

    private OrderItemDto toOrderItemDto(OrderDetails detail) {
        BigDecimal totalPrice = detail.getPriceUnit().multiply(BigDecimal.valueOf(detail.getQuantity()));

        PlateDto plateDto = PlateDto.builder()
                .id(detail.getPlate().getId())
                .name(detail.getPlate().getName())
                .price(detail.getPlate().getPrice())
                .build();

        return OrderItemDto.builder()
                .plateId(detail.getPlate().getId())
                .plate(plateDto)
                .quantity(detail.getQuantity())
                .priceUnit(detail.getPriceUnit())
                .totalPrice(totalPrice)
                .notes(detail.getNotes())
                .build();
    }
}