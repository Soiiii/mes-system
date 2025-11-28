package com.mes.messystem.service;

import com.mes.messystem.domain.*;
import com.mes.messystem.dto.ProductionStatistics;
import com.mes.messystem.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final LotRepository lotRepository;
    private final LotHistoryRepository lotHistoryRepository;
    private final QualityInspectionRepository qualityInspectionRepository;

    /**
     * 전체 생산 통계 계산
     */
    public ProductionStatistics getProductionStatistics() {
        List<Lot> allLots = lotRepository.findAll();
        List<LotHistory> allHistories = lotHistoryRepository.findAll();
        List<QualityInspection> allInspections = qualityInspectionRepository.findAll();

        // LOT 통계
        int totalLots = allLots.size();
        int completedLots = (int) allLots.stream()
                .filter(lot -> lot.getStatus() == LotStatus.COMPLETED)
                .count();
        int inProgressLots = (int) allLots.stream()
                .filter(lot -> lot.getStatus() == LotStatus.IN_PROGRESS)
                .count();

        // 생산량 통계
        int totalProduced = allHistories.stream()
                .mapToInt(h -> h.getOutputQuantity() != null ? h.getOutputQuantity() : 0)
                .sum();
        
        int totalDefects = allHistories.stream()
                .mapToInt(h -> h.getDefectQuantity() != null ? h.getDefectQuantity() : 0)
                .sum();

        double overallDefectRate = (totalProduced + totalDefects) > 0
                ? (double) totalDefects / (totalProduced + totalDefects) * 100
                : 0.0;

        // 품질 검사 통계
        int totalInspections = allInspections.size();
        int passedInspections = (int) allInspections.stream()
                .filter(i -> i.getResult() == InspectionResult.PASS)
                .count();
        int failedInspections = (int) allInspections.stream()
                .filter(i -> i.getResult() == InspectionResult.FAIL)
                .count();

        double inspectionPassRate = totalInspections > 0
                ? (double) passedInspections / totalInspections * 100
                : 0.0;

        // OEE 계산 (간소화 버전)
        // Availability: 가동률 (실제로는 계획 시간 대비 가동 시간)
        double availability = completedLots > 0 
                ? Math.min(95.0 + (Math.random() * 5), 100.0)  // 시뮬레이션: 95-100%
                : 0.0;

        // Performance: 성능률 (실제로는 표준 사이클 타임 대비 실제 사이클 타임)
        double performance = totalProduced > 0
                ? Math.min(85.0 + (Math.random() * 10), 100.0)  // 시뮬레이션: 85-95%
                : 0.0;

        // Quality: 품질률 (양품률)
        double quality = (totalProduced + totalDefects) > 0
                ? ((double) totalProduced / (totalProduced + totalDefects)) * 100
                : 0.0;

        // OEE = Availability × Performance × Quality
        double oee = (availability / 100) * (performance / 100) * (quality / 100) * 100;

        return ProductionStatistics.builder()
                .totalLots(totalLots)
                .completedLots(completedLots)
                .inProgressLots(inProgressLots)
                .totalProduced(totalProduced)
                .totalDefects(totalDefects)
                .overallDefectRate(Math.round(overallDefectRate * 100.0) / 100.0)
                .availability(Math.round(availability * 100.0) / 100.0)
                .performance(Math.round(performance * 100.0) / 100.0)
                .quality(Math.round(quality * 100.0) / 100.0)
                .oee(Math.round(oee * 100.0) / 100.0)
                .totalInspections(totalInspections)
                .passedInspections(passedInspections)
                .failedInspections(failedInspections)
                .inspectionPassRate(Math.round(inspectionPassRate * 100.0) / 100.0)
                .build();
    }
}
