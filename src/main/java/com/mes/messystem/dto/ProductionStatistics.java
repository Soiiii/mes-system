package com.mes.messystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionStatistics {
    // 전체 통계
    private Integer totalLots;
    private Integer completedLots;
    private Integer inProgressLots;
    
    // 생산량 통계
    private Integer totalProduced;
    private Integer totalDefects;
    private Double overallDefectRate;
    
    // OEE 관련
    private Double availability;      // 가동률
    private Double performance;       // 성능률
    private Double quality;          // 품질률
    private Double oee;              // Overall Equipment Effectiveness
    
    // 품질 검사 통계
    private Integer totalInspections;
    private Integer passedInspections;
    private Integer failedInspections;
    private Double inspectionPassRate;
}
