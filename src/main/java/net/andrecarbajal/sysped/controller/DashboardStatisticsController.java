package net.andrecarbajal.sysped.controller;

import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.dto.RevenueByPeriodDto;
import net.andrecarbajal.sysped.dto.StatisticsSummaryDto;
import net.andrecarbajal.sysped.dto.TopSellingPlateDto;
import net.andrecarbajal.sysped.service.StatisticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dashboard/statistics")
@RequiredArgsConstructor
public class DashboardStatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/summary")
    public ResponseEntity<StatisticsSummaryDto> getStatisticsSummary() {
        StatisticsSummaryDto summary = statisticsService.getStatisticsSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/revenue/daily")
    public ResponseEntity<List<RevenueByPeriodDto>> getRevenueByDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<RevenueByPeriodDto> revenue = statisticsService.getRevenueByDay(startDate, endDate);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/revenue/monthly")
    public ResponseEntity<List<RevenueByPeriodDto>> getRevenueByMonth(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<RevenueByPeriodDto> revenue = statisticsService.getRevenueByMonth(startDate, endDate);
        return ResponseEntity.ok(revenue);
    }


    @GetMapping("/peak-days")
    public ResponseEntity<List<RevenueByPeriodDto>> getPeakDays(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<RevenueByPeriodDto> peakDays = statisticsService.getDaysWithMostPlatesSold(
                startDate,
                endDate,
                limit
        );
        return ResponseEntity.ok(peakDays);
    }

    @GetMapping("/top-plates/quantity")
    public ResponseEntity<List<TopSellingPlateDto>> getTopPlatesByQuantity(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit
    ) {
        try {
            List<TopSellingPlateDto> topPlates = statisticsService.getTopSellingPlatesByQuantity(
                    startDate,
                    endDate,
                    limit
            );
            return ResponseEntity.ok(topPlates);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/top-plates/revenue")
    public ResponseEntity<List<TopSellingPlateDto>> getTopPlatesByRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit
    ) {
        try {
            List<TopSellingPlateDto> topPlates = statisticsService.getTopSellingPlatesByRevenue(
                    startDate,
                    endDate,
                    limit
            );
            return ResponseEntity.ok(topPlates);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}