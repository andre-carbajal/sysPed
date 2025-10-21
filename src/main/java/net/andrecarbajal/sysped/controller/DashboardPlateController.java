package net.andrecarbajal.sysped.controller;

import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.service.PlateService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;

@Controller
@RequestMapping("/dashboard/plate")
@RequiredArgsConstructor
public class DashboardPlateController {
    private final PlateService plateService;

    @PostMapping("/set-active")
    @ResponseBody
    public String setPlateActive(@RequestParam Long plateId, @RequestParam boolean active) {
        try {
            plateService.setPlateActive(plateId, active);
            return "OK";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public String updatePlate(@RequestParam Long id, @RequestParam String name, @RequestParam String description, @RequestParam BigDecimal price, @RequestParam String imageBase64, @RequestParam boolean active) {
        try {
            plateService.updatePlate(id, name, description, price, imageBase64, active);
            return "OK";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}
