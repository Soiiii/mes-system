package com.mes.messystem.service;

import com.mes.messystem.domain.*;
import com.mes.messystem.dto.*;
import com.mes.messystem.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final LotHistoryRepository lotHistoryRepository;
    private final EquipmentDataRepository equipmentDataRepository;
    private final WorkOrderRepository workOrderRepository;
    private final EquipmentRepository equipmentRepository;
    private final LotRepository lotRepository;

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

        List<LotHistory> todayHistories = lotHistoryRepository.findAll().stream()
                .filter(lh -> lh.getProcessedAt() != null)
                .filter(lh -> lh.getProcessedAt().isAfter(startOfDay) && lh.getProcessedAt().isBefore(endOfDay))
                .toList();

        int totalProduced = todayHistories.stream()
                .mapToInt(lh -> lh.getOutputQuantity() != null ? lh.getOutputQuantity() : 0)
                .sum();
        
        int totalDefects = todayHistories.stream()
                .mapToInt(lh -> lh.getDefectQuantity() != null ? lh.getDefectQuantity() : 0)
                .sum();
        
        int total = totalProduced + totalDefects;
        double defectRate = total > 0 ? (double) totalDefects / total * 100 : 0.0;

        return TodayProductionStats.builder()
                .totalGoodQty(totalProduced)
                .totalBadQty(totalDefects)
                .totalQty(total)
                .defectRate(defectRate)
                .build();
    }

    /**
     * Calculate defect rate by product
     */
    public List<ProductDefectRate> getProductDefectRates() {
        List<LotHistory> allHistories = lotHistoryRepository.findAll();

        Map<Long, List<LotHistory>> historiesByProduct = allHistories.stream()
                .filter(lh -> lh.getLot() != null && lh.getLot().getProduct() != null)
                .collect(Collectors.groupingBy(lh -> lh.getLot().getProduct().getId()));

        return historiesByProduct.entrySet().stream()
                .map(entry -> {
                    Long productId = entry.getKey();
                    List<LotHistory> histories = entry.getValue();

                    String productName = histories.get(0).getLot().getProduct().getName();
                    int totalProduced = histories.stream()
                            .mapToInt(lh -> lh.getOutputQuantity() != null ? lh.getOutputQuantity() : 0)
                            .sum();
                    int totalDefects = histories.stream()
                            .mapToInt(lh -> lh.getDefectQuantity() != null ? lh.getDefectQuantity() : 0)
                            .sum();
                    int total = totalProduced + totalDefects;

                    double defectRate = total > 0 ? (double) totalDefects / total * 100 : 0.0;

                    return ProductDefectRate.builder()
                            .productId(productId)
                            .productName(productName)
                            .totalGoodQty(totalProduced)
                            .totalBadQty(totalDefects)
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
                            .status(equipment.getStatus())
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
                    // LOT 기반으로 진행률 계산
                    List<Lot> lots = lotRepository.findByWorkOrderId(wo.getId());
                    int totalLots = lots.size();
                    int completedLots = (int) lots.stream()
                            .filter(lot -> lot.getStatus() == LotStatus.COMPLETED)
                            .count();

                    double progressPercentage = totalLots > 0
                            ? (double) completedLots / totalLots * 100
                            : 0.0;

                    return WorkProgressInfo.builder()
                            .workOrderId(wo.getId())
                            .productName(wo.getProduct().getName())
                            .status(wo.getStatus())
                            .totalProcesses(totalLots)
                            .completedProcesses(completedLots)
                            .progressPercentage(Math.round(progressPercentage * 100.0) / 100.0)
                            .build();
                })
                .sorted(Comparator.comparing(WorkProgressInfo::getProgressPercentage).reversed())
                .toList();
    }
}
