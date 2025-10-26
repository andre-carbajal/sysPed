package net.andrecarbajal.sysped.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.controller.PlateStatusWebSocketController;
import net.andrecarbajal.sysped.dto.CreateOrderRequestDto;
import net.andrecarbajal.sysped.dto.ItemOrderDto;
import net.andrecarbajal.sysped.model.*;
import net.andrecarbajal.sysped.repository.OrderRepository;
import net.andrecarbajal.sysped.repository.PlateRepository;
import net.andrecarbajal.sysped.repository.StaffRepository;
import net.andrecarbajal.sysped.repository.TableRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final PlateRepository plateRepository;
    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final PlateStatusWebSocketController plateStatusWebSocketController;
    private final StaffRepository staffRepository;

    private final TableService tableService;

    @Transactional
    public Order createOrder(Long tableId, CreateOrderRequestDto requestDto, String dnistaff) {
        Table table = tableRepository.findById(tableId).orElseThrow(()-> new RuntimeException("Mesa no encontrada"));
        Staff staff = staffRepository.findByDni(dnistaff).orElseThrow(() -> new RuntimeException("Mozo no encontrado"));

        Order newOrder = new Order();
        newOrder.setTable(table);
        newOrder.setStaff(staff);
        newOrder.setStatus(OrderStatus.PENDIENTE);

        BigDecimal total = BigDecimal.ZERO;

        for (ItemOrderDto itemDto : requestDto.items()) {
            Plate plate = plateRepository.findById(itemDto.plateId())
                    .orElseThrow(() -> new RuntimeException("Plato no encontrado con ID: " + itemDto.plateId()));

            if (!plate.isActive()) {
                throw new RuntimeException("El plato '" + plate.getName() + "' no está disponible.");
            }

            OrderDetails orderDetail = new OrderDetails();
            orderDetail.setPlate(plate);
            orderDetail.setQuantity(itemDto.quantity());
            orderDetail.setPriceUnit(plate.getPrice());
            orderDetail.setNotes(itemDto.notes() != null ? itemDto.notes() : "");

            total = total.add(plate.getPrice().multiply(BigDecimal.valueOf(itemDto.quantity())));

            plateRepository.save(plate);

            newOrder.addOrderDetail(orderDetail);
        }

        newOrder.setPriceTotal(total);
        Order savedOrder = orderRepository.save(newOrder);

        tableService.updateTableStatus(table.getNumber(), TableStatus.ESPERANDO_PEDIDO);
        return savedOrder;
    }
}
