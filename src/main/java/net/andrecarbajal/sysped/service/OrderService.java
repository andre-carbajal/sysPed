package net.andrecarbajal.sysped.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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