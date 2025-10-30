package net.andrecarbajal.sysped.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.dto.OrderCreateRequestDto;
import net.andrecarbajal.sysped.dto.OrderCreateResponseDto;
import net.andrecarbajal.sysped.dto.PlateListDto;
import net.andrecarbajal.sysped.service.OrderService;
import net.andrecarbajal.sysped.service.PlateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dashboard/orders")
@RequiredArgsConstructor
public class DashboardOrderController {

    private final OrderService orderService;
    private final PlateService plateService;

    @PostMapping
    public ResponseEntity<OrderCreateResponseDto> createOrder(@Valid @RequestBody OrderCreateRequestDto request) {
        try {
            OrderCreateResponseDto response = orderService.createOrder(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<java.util.List<net.andrecarbajal.sysped.dto.OrderDto>> listOrders(@RequestParam(required = false) String status) {
        try {
            String filter = status;
            // Si es cocinero y no se recibió filtro, devolvemos pendientes y en preparacion
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            boolean isCocinero = false;
            if (auth != null && auth.getAuthorities() != null) {
                isCocinero = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().toUpperCase().contains("COCINERO"));
            }
            if ((filter == null || filter.isBlank()) && isCocinero) {
                filter = "PENDIENTE,EN_PREPARACION";
            }
            java.util.List<net.andrecarbajal.sysped.dto.OrderDto> list = orderService.listOrders(filter == null ? "ALL" : filter);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{orderId}/status")
    public ResponseEntity<Object> changeOrderStatus(@PathVariable Long orderId, @RequestBody net.andrecarbajal.sysped.dto.OrderStatusChangeRequestDto body) {
        try {
            net.andrecarbajal.sysped.model.OrderStatus st = net.andrecarbajal.sysped.model.OrderStatus.valueOf(body.status());
            net.andrecarbajal.sysped.dto.OrderDto updated = orderService.updateOrderStatus(orderId, st);
            // Se notificará por WebSocket desde el servicio (OrderService)
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Estado inválido: " + body.status());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : "Error interno al actualizar pedido";
            return ResponseEntity.status(500).body(msg);
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<net.andrecarbajal.sysped.dto.OrderDto> getOrder(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/plates")
    public ResponseEntity<List<PlateListDto>> getAvailablePlates() {
        try {
            List<PlateListDto> plates = plateService.findAllActivePlates();
            return ResponseEntity.ok(plates);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}