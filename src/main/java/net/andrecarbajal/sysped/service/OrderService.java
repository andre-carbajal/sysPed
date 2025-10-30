package net.andrecarbajal.sysped.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.controller.OrderWebSocketController;
import net.andrecarbajal.sysped.dto.OrderCreateRequestDto;
import net.andrecarbajal.sysped.dto.OrderCreateResponseDto;
import net.andrecarbajal.sysped.dto.OrderItemResponseDto;
import net.andrecarbajal.sysped.model.*;
import net.andrecarbajal.sysped.repository.OrderRepository;
import net.andrecarbajal.sysped.repository.PlateRepository;
import net.andrecarbajal.sysped.repository.StaffRepository;
import net.andrecarbajal.sysped.repository.TableRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final PlateRepository plateRepository;
    private final StaffRepository staffRepository;
    private final TableService tableService;
    private final OrderWebSocketController orderWebSocketController;

    @Transactional
    public OrderCreateResponseDto createOrder(OrderCreateRequestDto request) {
        RestaurantTable restaurantTable = tableRepository.findByNumber(request.tableNumber())
                .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada: " + request.tableNumber()));

        if (restaurantTable.getStatus() != TableStatus.DISPONIBLE) {
            throw new IllegalStateException("La mesa " + request.tableNumber() + " no está disponible para pedidos");
        }

        Staff currentStaff = getCurrentStaff();

        Order order = new Order();
        order.setRestaurantTable(restaurantTable);
        order.setStaff(currentStaff);
        order.setStatus(OrderStatus.PENDIENTE);

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (var item : request.items()) {
            Plate plate = plateRepository.findById(item.plateId())
                    .orElseThrow(() -> new IllegalArgumentException("Plato no encontrado: " + item.plateId()));

            if (!Boolean.TRUE.equals(plate.isActive())) {
                throw new IllegalStateException("El plato " + plate.getName() + " no está disponible");
            }

            OrderDetails detail = new OrderDetails();
            detail.setOrder(order);
            detail.setPlate(plate);
            detail.setQuantity(item.quantity());
            detail.setPriceUnit(plate.getPrice());
            detail.setNotes(item.notes());

            order.addOrderDetail(detail);

            BigDecimal itemTotal = plate.getPrice().multiply(BigDecimal.valueOf(item.quantity()));
            totalPrice = totalPrice.add(itemTotal);
        }

        order.setPriceTotal(totalPrice);

        Order savedOrder = orderRepository.save(order);

        tableService.updateTableStatus(request.tableNumber(), TableStatus.ESPERANDO_PEDIDO);

        // notify websocket
        try {
            orderWebSocketController.sendOrderUpdate(new net.andrecarbajal.sysped.dto.OrderDto(
                    savedOrder.getId(),
                    savedOrder.getRestaurantTable().getNumber(),
                    savedOrder.getDateandtimeOrder(),
                    savedOrder.getStatus(),
                    savedOrder.getPriceTotal(),
                    savedOrder.getDetails().stream().map(d -> new net.andrecarbajal.sysped.dto.OrderItemResponseDto(
                            d.getPlate().getId(),
                            d.getPlate().getName(),
                            d.getQuantity(),
                            d.getPriceUnit(),
                            d.getPriceUnit().multiply(java.math.BigDecimal.valueOf(d.getQuantity())),
                            d.getNotes()
                    )).toList()
            ));
        } catch (Exception ignored) {}

        List<OrderItemResponseDto> itemResponses = savedOrder.getDetails().stream()
                .map(detail -> new OrderItemResponseDto(
                        detail.getPlate().getId(),
                        detail.getPlate().getName(),
                        detail.getQuantity(),
                        detail.getPriceUnit(),
                        detail.getPriceUnit().multiply(BigDecimal.valueOf(detail.getQuantity())),
                        detail.getNotes()
                ))
                .collect(Collectors.toList());

        return new OrderCreateResponseDto(
                savedOrder.getId(),
                savedOrder.getRestaurantTable().getNumber(),
                savedOrder.getDateandtimeOrder(),
                savedOrder.getStatus(),
                savedOrder.getPriceTotal(),
                itemResponses
        );
    }

    public java.util.List<net.andrecarbajal.sysped.dto.OrderDto> listOrders(String statusFilter) {
        java.util.List<net.andrecarbajal.sysped.model.Order> orders = orderRepository.findAll();
        java.util.stream.Stream<net.andrecarbajal.sysped.model.Order> stream = orders.stream();
        if (statusFilter != null && !"ALL".equalsIgnoreCase(statusFilter)) {
            // aceptar lista de estados separados por comas: e.g. "PENDIENTE,EN_PREPARACION"
            String[] parts = statusFilter.split(",");
            java.util.Set<net.andrecarbajal.sysped.model.OrderStatus> allowed = new java.util.HashSet<>();
            for (String p : parts) {
                String trimmed = p.trim();
                if (trimmed.isEmpty()) continue;
                try {
                    allowed.add(net.andrecarbajal.sysped.model.OrderStatus.valueOf(trimmed));
                } catch (IllegalArgumentException e) {
                    // ignorar valores inválidos
                }
            }
            if (!allowed.isEmpty()) {
                stream = stream.filter(o -> allowed.contains(o.getStatus()));
            } else {
                // si no hay estados válidos, devolver vacio
                return java.util.Collections.emptyList();
            }
        }
        return stream.map(o -> new net.andrecarbajal.sysped.dto.OrderDto(
                o.getId(),
                o.getRestaurantTable().getNumber(),
                o.getDateandtimeOrder(),
                o.getStatus(),
                o.getPriceTotal(),
                o.getDetails().stream().map(d -> new net.andrecarbajal.sysped.dto.OrderItemResponseDto(
                        d.getPlate().getId(),
                        d.getPlate().getName(),
                        d.getQuantity(),
                        d.getPriceUnit(),
                        d.getPriceUnit().multiply(java.math.BigDecimal.valueOf(d.getQuantity())),
                        d.getNotes()
                )).toList()
        )).toList();
    }

    @Transactional
    public net.andrecarbajal.sysped.dto.OrderDto updateOrderStatus(Long orderId, net.andrecarbajal.sysped.model.OrderStatus newStatus) {
        net.andrecarbajal.sysped.model.Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + orderId));
        // Validar transición de estado del pedido
        net.andrecarbajal.sysped.model.OrderStatus current = order.getStatus();
        java.util.Set<net.andrecarbajal.sysped.model.OrderStatus> allowed = switch (current) {
            case PENDIENTE -> java.util.Set.of(net.andrecarbajal.sysped.model.OrderStatus.EN_PREPARACION, net.andrecarbajal.sysped.model.OrderStatus.CANCELADO);
            case EN_PREPARACION -> java.util.Set.of(net.andrecarbajal.sysped.model.OrderStatus.LISTO, net.andrecarbajal.sysped.model.OrderStatus.CANCELADO);
            case LISTO -> java.util.Set.of(net.andrecarbajal.sysped.model.OrderStatus.PAGADO);
            default -> java.util.Set.of();
        };
        if (!allowed.contains(newStatus)) {
            throw new IllegalStateException("Operación no permitida: " + current + " -> " + newStatus);
        }
        order.setStatus(newStatus);
        net.andrecarbajal.sysped.model.Order saved = orderRepository.save(order);
        // Cuando el pedido pasa a LISTO, marcar la mesa como PEDIDO_ENTREGADO
        if (newStatus == net.andrecarbajal.sysped.model.OrderStatus.LISTO) {
            try {
                tableService.updateTableStatus(saved.getRestaurantTable().getNumber(), net.andrecarbajal.sysped.model.TableStatus.PEDIDO_ENTREGADO);
            } catch (IllegalStateException e) {
                // Si la transición de estado de mesa no está permitida desde su estado actual,
                // no queremos fallar toda la operación de cambiar el estado del pedido.
                // Registramos y continuamos sin propagar la excepción.
                System.err.println("No se pudo actualizar estado de mesa al marcar pedido LISTO: " + e.getMessage());
            }
        }
        try {
            orderWebSocketController.sendOrderUpdate(new net.andrecarbajal.sysped.dto.OrderDto(
                    saved.getId(),
                    saved.getRestaurantTable().getNumber(),
                    saved.getDateandtimeOrder(),
                    saved.getStatus(),
                    saved.getPriceTotal(),
                    saved.getDetails().stream().map(d -> new net.andrecarbajal.sysped.dto.OrderItemResponseDto(
                            d.getPlate().getId(),
                            d.getPlate().getName(),
                            d.getQuantity(),
                            d.getPriceUnit(),
                            d.getPriceUnit().multiply(java.math.BigDecimal.valueOf(d.getQuantity())),
                            d.getNotes()
                    )).toList()
            ));
        } catch (Exception ignored) {}
        return new net.andrecarbajal.sysped.dto.OrderDto(
                saved.getId(),
                saved.getRestaurantTable().getNumber(),
                saved.getDateandtimeOrder(),
                saved.getStatus(),
                saved.getPriceTotal(),
                saved.getDetails().stream().map(d -> new net.andrecarbajal.sysped.dto.OrderItemResponseDto(
                        d.getPlate().getId(),
                        d.getPlate().getName(),
                        d.getQuantity(),
                        d.getPriceUnit(),
                        d.getPriceUnit().multiply(java.math.BigDecimal.valueOf(d.getQuantity())),
                        d.getNotes()
                )).toList()
        );
    }

    public java.util.Optional<net.andrecarbajal.sysped.dto.OrderDto> getOrderById(Long orderId) {
        return orderRepository.findById(orderId).map(o -> new net.andrecarbajal.sysped.dto.OrderDto(
                o.getId(),
                o.getRestaurantTable().getNumber(),
                o.getDateandtimeOrder(),
                o.getStatus(),
                o.getPriceTotal(),
                o.getDetails().stream().map(d -> new net.andrecarbajal.sysped.dto.OrderItemResponseDto(
                        d.getPlate().getId(),
                        d.getPlate().getName(),
                        d.getQuantity(),
                        d.getPriceUnit(),
                        d.getPriceUnit().multiply(java.math.BigDecimal.valueOf(d.getQuantity())),
                        d.getNotes()
                )).toList()
        ));
    }

    private Staff getCurrentStaff() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Usuario no autenticado");
        }
        String dni = auth.getName();
        return staffRepository.findByDni(dni)
                .orElseThrow(() -> new IllegalStateException("Staff no encontrado: " + dni));
    }
}