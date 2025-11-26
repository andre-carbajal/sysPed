package net.andrecarbajal.sysped.service;

import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.dto.RevenueByPeriodDto;
import net.andrecarbajal.sysped.dto.StatisticsSummaryDto;
import net.andrecarbajal.sysped.dto.TopSellingPlateDto;
import net.andrecarbajal.sysped.dto.WaiterPerformanceDto;
import net.andrecarbajal.sysped.repository.OrderDetailsRepository;
import net.andrecarbajal.sysped.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailsRepository;

    public StatisticsSummaryDto getStatisticsSummary() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDateTime startOfMonth = firstDayOfMonth.atStartOfDay();
        LocalDateTime endOfMonth = today.atTime(LocalTime.MAX);

        BigDecimal todayRevenue = orderRepository.getTotalRevenueInPeriod(startOfDay, endOfDay);
        Long todayOrders = orderRepository.getOrderCountInPeriod(startOfDay, endOfDay);

        BigDecimal monthRevenue = orderRepository.getTotalRevenueInPeriod(startOfMonth, endOfMonth);
        Long monthOrders = orderRepository.getOrderCountInPeriod(startOfMonth, endOfMonth);

        BigDecimal averageTicket = BigDecimal.ZERO;
        if (monthOrders > 0) {
            averageTicket = monthRevenue.divide(
                    BigDecimal.valueOf(monthOrders),
                    2,
                    RoundingMode.HALF_UP
            );
        }

        List<Map<String, Object>> topPlates = orderDetailsRepository.findTopSellingPlatesByQuantity(
                startOfMonth,
                endOfMonth
        );
        String topSellingPlate = "N/A";
        if (!topPlates.isEmpty()) {
            Map<String, Object> topPlate = topPlates.get(0);
            topSellingPlate = (String) topPlate.get("plateName");
        }

        List<Object> peakDays = orderRepository.findDaysWithMostPlatesSold(
                startOfMonth,
                endOfMonth
        );
        LocalDate peakSalesDate = null;
        BigDecimal peakSalesAmount = BigDecimal.ZERO;
        if (!peakDays.isEmpty()) {
            Map<String, Object> peakDay = (Map<String, Object>) peakDays.get(0);
            peakSalesDate = (LocalDate) peakDay.get("date");

            LocalDateTime peakStart = peakSalesDate.atStartOfDay();
            LocalDateTime peakEnd = peakSalesDate.atTime(LocalTime.MAX);
            peakSalesAmount = orderRepository.getTotalRevenueInPeriod(peakStart, peakEnd);
        }

        return StatisticsSummaryDto.builder()
                .todayRevenue(todayRevenue)
                .monthRevenue(monthRevenue)
                .todayOrders(todayOrders)
                .monthOrders(monthOrders)
                .averageTicket(averageTicket)
                .topSellingPlate(topSellingPlate)
                .peakSalesDate(peakSalesDate)
                .peakSalesAmount(peakSalesAmount)
                .build();
    }

    public List<RevenueByPeriodDto> getRevenueByDay(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<Object> results = orderRepository.findRevenueByDay(start, end);

        return results.stream()
                .map(obj -> {
                    Map<String, Object> map = (Map<String, Object>) obj;
                    LocalDate date = (LocalDate) map.get("date");
                    BigDecimal totalRevenue = (BigDecimal) map.get("totalRevenue");
                    Long orderCount = (Long) map.get("orderCount");

                    BigDecimal averageTicket = BigDecimal.ZERO;
                    if (orderCount > 0) {
                        averageTicket = totalRevenue.divide(
                                BigDecimal.valueOf(orderCount),
                                2,
                                RoundingMode.HALF_UP
                        );
                    }

                    return RevenueByPeriodDto.builder()
                            .date(date)
                            .period(date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                            .totalRevenue(totalRevenue)
                            .orderCount(orderCount)
                            .averageTicket(averageTicket)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<RevenueByPeriodDto> getRevenueByMonth(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<Object> results = orderRepository.findRevenueByMonth(start, end);

        return results.stream()
                .map(obj -> {
                    Map<String, Object> map = (Map<String, Object>) obj;
                    String period = (String) map.get("period");
                    BigDecimal totalRevenue = (BigDecimal) map.get("totalRevenue");
                    Long orderCount = (Long) map.get("orderCount");

                    YearMonth yearMonth = YearMonth.parse(period);
                    LocalDate date = yearMonth.atDay(1);

                    BigDecimal averageTicket = BigDecimal.ZERO;
                    if (orderCount > 0) {
                        averageTicket = totalRevenue.divide(
                                BigDecimal.valueOf(orderCount),
                                2,
                                RoundingMode.HALF_UP
                        );
                    }

                    return RevenueByPeriodDto.builder()
                            .date(date)
                            .period(period)
                            .totalRevenue(totalRevenue)
                            .orderCount(orderCount)
                            .averageTicket(averageTicket)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<RevenueByPeriodDto> getDaysWithMostPlatesSold(
            LocalDate startDate,
            LocalDate endDate,
            int limit
    ) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<Object> results = orderRepository.findDaysWithMostPlatesSold(start, end);

        return results.stream()
                .limit(limit)
                .map(obj -> {
                    Map<String, Object> map = (Map<String, Object>) obj;
                    LocalDate date = (LocalDate) map.get("date");
                    Long totalQuantity = (Long) map.get("totalQuantity");

                    LocalDateTime dayStart = date.atStartOfDay();
                    LocalDateTime dayEnd = date.atTime(LocalTime.MAX);
                    BigDecimal dayRevenue = orderRepository.getTotalRevenueInPeriod(dayStart, dayEnd);
                    Long dayOrders = orderRepository.getOrderCountInPeriod(dayStart, dayEnd);

                    return RevenueByPeriodDto.builder()
                            .date(date)
                            .period(date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                            .totalRevenue(dayRevenue)
                            .orderCount(totalQuantity)
                            .averageTicket(dayOrders > 0 ?
                                    dayRevenue.divide(BigDecimal.valueOf(dayOrders), 2, RoundingMode.HALF_UP) :
                                    BigDecimal.ZERO)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<TopSellingPlateDto> getTopSellingPlatesByQuantity(
            LocalDate startDate,
            LocalDate endDate,
            int limit
    ) {
        try {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(LocalTime.MAX);

            List<Map<String, Object>> results = orderDetailsRepository.findTopSellingPlatesByQuantity(start, end);

            Long totalPlatesSold = orderDetailsRepository.getTotalPlatesSoldInPeriod(start, end);

            if (totalPlatesSold == null || totalPlatesSold == 0) {
                return new ArrayList<>();
            }

            List<TopSellingPlateDto> topPlates = results.stream()
                    .limit(limit)
                    .map(map -> {
                        try {
                            Long plateId = (Long) map.get("plateId");
                            String plateName = (String) map.get("plateName");
                            String categoryName = (String) map.get("categoryName");
                            Long quantitySold = (Long) map.get("quantitySold");

                            Object revenueObj = map.get("totalRevenue");
                            BigDecimal totalRevenue = revenueObj instanceof BigDecimal
                                    ? (BigDecimal) revenueObj
                                    : BigDecimal.valueOf(((Number) revenueObj).doubleValue());

                            Object priceObj = map.get("averagePrice");
                            BigDecimal averagePrice = priceObj instanceof BigDecimal
                                    ? (BigDecimal) priceObj
                                    : BigDecimal.valueOf(((Number) priceObj).doubleValue());

                            Double percentageOfTotal = (quantitySold.doubleValue() / totalPlatesSold.doubleValue()) * 100;

                            return TopSellingPlateDto.builder()
                                    .plateId(plateId)
                                    .plateName(plateName)
                                    .categoryName(categoryName != null ? categoryName : "Sin categoría")
                                    .quantitySold(quantitySold)
                                    .totalRevenue(totalRevenue)
                                    .averagePrice(averagePrice)
                                    .percentageOfTotal(Math.round(percentageOfTotal * 100.0) / 100.0)
                                    .build();
                        } catch (Exception e) {
                            throw new RuntimeException("Error al procesar datos del plato", e);
                        }
                    })
                    .collect(Collectors.toList());

            return topPlates;

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener estadísticas de platos más vendidos", e);
        }
    }

    public List<TopSellingPlateDto> getTopSellingPlatesByRevenue(
            LocalDate startDate,
            LocalDate endDate,
            int limit
    ) {
        try {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(LocalTime.MAX);

            List<Map<String, Object>> results = orderDetailsRepository.findTopSellingPlatesByRevenue(start, end);

            Long totalPlatesSold = orderDetailsRepository.getTotalPlatesSoldInPeriod(start, end);

            if (totalPlatesSold == null || totalPlatesSold == 0) {
                return new ArrayList<>();
            }

            List<TopSellingPlateDto> topPlates = results.stream()
                    .limit(limit)
                    .map(map -> {
                        try {
                            Long plateId = (Long) map.get("plateId");
                            String plateName = (String) map.get("plateName");
                            String categoryName = (String) map.get("categoryName");
                            Long quantitySold = (Long) map.get("quantitySold");

                            Object revenueObj = map.get("totalRevenue");
                            BigDecimal totalRevenue = revenueObj instanceof BigDecimal
                                    ? (BigDecimal) revenueObj
                                    : BigDecimal.valueOf(((Number) revenueObj).doubleValue());

                            Object priceObj = map.get("averagePrice");
                            BigDecimal averagePrice = priceObj instanceof BigDecimal
                                    ? (BigDecimal) priceObj
                                    : BigDecimal.valueOf(((Number) priceObj).doubleValue());

                            Double percentageOfTotal = (quantitySold.doubleValue() / totalPlatesSold.doubleValue()) * 100;

                            return TopSellingPlateDto.builder()
                                    .plateId(plateId)
                                    .plateName(plateName)
                                    .categoryName(categoryName != null ? categoryName : "Sin categoría")
                                    .quantitySold(quantitySold)
                                    .totalRevenue(totalRevenue)
                                    .averagePrice(averagePrice)
                                    .percentageOfTotal(Math.round(percentageOfTotal * 100.0) / 100.0)
                                    .build();
                        } catch (Exception e) {
                            throw new RuntimeException("Error al procesar datos del plato", e);
                        }
                    })
                    .collect(Collectors.toList());

            return topPlates;

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener estadísticas de platos más vendidos", e);
        }
    }

    public List<WaiterPerformanceDto> getWaiterPerformance(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<Object> results = orderRepository.findWaiterPerformance(start, end);

        return results.stream()
                .map(obj -> {
                    Map<String, Object> map = (Map<String, Object>) obj;
                    String staffDni = (String) map.get("staffDni");
                    String staffName = (String) map.get("staffName");
                    Long tablesServed = (Long) map.get("tablesServed");
                    Long totalOrders = (Long) map.get("totalOrders");
                    BigDecimal totalRevenue = (BigDecimal) map.get("totalRevenue");

                    BigDecimal averagePerTable = BigDecimal.ZERO;
                    if (tablesServed > 0) {
                        averagePerTable = totalRevenue.divide(
                                BigDecimal.valueOf(tablesServed),
                                2,
                                RoundingMode.HALF_UP
                        );
                    }

                    Double performanceScore = calculatePerformanceScore(
                            tablesServed,
                            totalOrders,
                            totalRevenue
                    );

                    return WaiterPerformanceDto.builder()
                            .staffDni(staffDni)
                            .staffName(staffName)
                            .tablesServed(tablesServed)
                            .totalOrders(totalOrders)
                            .totalRevenue(totalRevenue)
                            .averagePerTable(averagePerTable)
                            .performanceScore(performanceScore)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private Double calculatePerformanceScore(
            Long tablesServed,
            Long totalOrders,
            BigDecimal totalRevenue
    ) {
        double tableWeight = 0.4;
        double orderWeight = 0.3;
        double revenueWeight = 0.3;

        double normalizedTables = Math.min(tablesServed / 50.0, 1.0);
        double normalizedOrders = Math.min(totalOrders / 100.0, 1.0);
        double normalizedRevenue = Math.min(totalRevenue.doubleValue() / 5000.0, 1.0);

        double score = (normalizedTables * tableWeight +
                normalizedOrders * orderWeight +
                normalizedRevenue * revenueWeight) * 100;

        return Math.round(score * 100.0) / 100.0;
    }
}