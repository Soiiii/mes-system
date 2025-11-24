package com.mes.messystem.service;

import com.mes.messystem.domain.*;
import com.mes.messystem.dto.*;
import com.mes.messystem.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final WorkResultRepository workResultRepository;
    private final EquipmentDataRepository equipmentDataRepository;
    private final WorkOrderRepository workOrderRepository;
    private final EquipmentRepository equipmentRepository;

    /**
     * Get comprehensive dashboard data
     */
    public DashboardResponse getDashboardData() {
        return DashboardResponse.builder()
                .todayProduction(getTodayProductionStats())
                .productDefectRates(getProductDefectRates())
                .equipmentStatuses(getEquipmentStatuses())
                .workProgresses(getWorkProgresses())
                .build();
    }

    /**
     * Calculate today's total production statistics
     */
    public TodayProductionStats getTodayProductionStats() {
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        List<WorkResult> todayResults = workResultRepository.findAll().stream()
                .filter(wr -> wr.getWorkTime() != null)
                .filter(wr -> wr.getWorkTime().isAfter(startOfDay) && wr.getWorkTime().isBefore(endOfDay))
                .toList();

        int totalGood = todayResults.stream().mapToInt(WorkResult::getGoodQty).sum();
        int totalBad = todayResults.stream().mapToInt(WorkResult::getBadQty).sum();
        int total = totalGood + totalBad;

        double defectRate = total > 0 ? (double) totalBad / total : 0.0;

        return TodayProductionStats.builder()
                .totalGoodQty(totalGood)
                .totalBadQty(totalBad)
                .totalQty(total)
                .defectRate(defectRate)
                .build();
    }

    /**
     * Calculate defect rate by product
     */
    public List<ProductDefectRate> getProductDefectRates() {
        List<WorkResult> allResults = workResultRepository.findAll();

        Map<Long, List<WorkResult>> resultsByProduct = allResults.stream()
                .filter(wr -> wr.getWorkOrder() != null && wr.getWorkOrder().getProduct() != null)
                .collect(Collectors.groupingBy(wr -> wr.getWorkOrder().getProduct().getId()));

        return resultsByProduct.entrySet().stream()
                .map(entry -> {
                    Long productId = entry.getKey();
                    List<WorkResult> results = entry.getValue();

                    String productName = results.get(0).getWorkOrder().getProduct().getName();
                    int totalGood = results.stream().mapToInt(WorkResult::getGoodQty).sum();
                    int totalBad = results.stream().mapToInt(WorkResult::getBadQty).sum();
                    int total = totalGood + totalBad;

                    double defectRate = total > 0 ? (double) totalBad / total : 0.0;

                    return ProductDefectRate.builder()
                            .productId(productId)
                            .productName(productName)
                            .totalGoodQty(totalGood)
                            .totalBadQty(totalBad)
                            .defectRate(defectRate)
                            .build();
                })
                .sorted(Comparator.comparing(ProductDefectRate::getDefectRate).reversed())
                .toList();
    }

    /**
     * Get current equipment statuses
     */
    public List<EquipmentStatusSummary> getEquipmentStatuses() {
        List<Equipment> equipments = equipmentRepository.findAll();

        return equipments.stream()
                .map(equipment -> {
                    List<EquipmentData> recentData = equipmentDataRepository
                            .findByEquipmentIdOrderByTimestampDesc(equipment.getId());

                    EquipmentData latest = recentData.isEmpty() ? null : recentData.get(0);

                    return EquipmentStatusSummary.builder()
                            .equipmentId(equipment.getId())
                            .equipmentName(equipment.getName())
                            .location(equipment.getLocation())
                            .status(latest != null ? latest.getStatus() : null)
                            .temperature(latest != null ? latest.getTemperature() : null)
                            .productionSpeed(latest != null ? latest.getProductionSpeed() : null)
                            .lastUpdated(latest != null ? latest.getTimestamp() : null)
                            .build();
                })
                .toList();
    }

    /**
     * Get work order progress information
     */
    public List<WorkProgressInfo> getWorkProgresses() {
        List<WorkOrder> workOrders = workOrderRepository.findAll();

        return workOrders.stream()
                .filter(wo -> wo.getProduct() != null)
                .map(wo -> {
                    int totalProcesses = wo.getProduct().getProcesses().size();
                    List<WorkResult> completed = workResultRepository.findByWorkOrderId(wo.getId());
                    int completedProcesses = completed.size();

                    double progressPercentage = totalProcesses > 0
                            ? (double) completedProcesses / totalProcesses * 100
                            : 0.0;

                    return WorkProgressInfo.builder()
                            .workOrderId(wo.getId())
                            .productName(wo.getProduct().getName())
                            .status(wo.getStatus())
                            .totalProcesses(totalProcesses)
                            .completedProcesses(completedProcesses)
                            .progressPercentage(Math.round(progressPercentage * 100.0) / 100.0)
                            .build();
                })
                .sorted(Comparator.comparing(WorkProgressInfo::getProgressPercentage).reversed())
                .toList();
    }
}
