package net.andrecarbajal.sysped.controller;

import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.model.TableStatus;
import net.andrecarbajal.sysped.service.TableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard/tables")
@RequiredArgsConstructor
public class DashboardTableController {

    private final TableService tableService;

    @GetMapping("/{tableNumber}/allowed-statuses")
    public ResponseEntity<List<String>> getAllowedStatuses(@PathVariable Integer tableNumber) {
        try {
            Set<TableStatus> allowed = tableService.getAllowedStatuses(tableNumber);
            List<String> allowedNames = allowed.stream().map(Enum::name).collect(Collectors.toList());
            return ResponseEntity.ok(allowedNames);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

