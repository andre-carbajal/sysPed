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