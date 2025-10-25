package net.andrecarbajal.sysped.controller;

import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.model.TableStatus;
import net.andrecarbajal.sysped.service.TableService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/dashboard/mesas")
@RequiredArgsConstructor
public class DashboardTableController {

    private final TableService tableService;

    @PostMapping("/update-status")
    @ResponseBody
    public String updateTableStatus(@RequestParam Integer tableNumber,
                                    @RequestParam String status) {
        try {
            TableStatus newStatus = TableStatus.valueOf(status);

            tableService.updateTableStatus(tableNumber, newStatus);
            return "OK";
        } catch (IllegalArgumentException e) {
            return "ERROR: Estado no válido: " + status;
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}