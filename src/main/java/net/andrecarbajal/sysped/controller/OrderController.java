package net.andrecarbajal.sysped.controller;

import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.dto.CreateOrderRequestDto;
import net.andrecarbajal.sysped.service.OrderService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MOZO')")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/crear")
    public ResponseEntity<String> createOrder(
            @PathVariable Long tableId,
            @RequestBody CreateOrderRequestDto requestDto,
            Authentication authentication) {
        try {
            String dniMozo = authentication.name();
            orderService.createOrder(tableId, requestDto, dniMozo);
            return ResponseEntity.ok("Pedido creado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear la orden: " + e.getMessage());
        }
    }
}
