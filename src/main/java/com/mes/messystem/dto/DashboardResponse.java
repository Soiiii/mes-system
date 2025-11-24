package com.mes.messystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {
    private TodayProductionStats todayProduction;
    private List<ProductDefectRate> productDefectRates;
    private List<EquipmentStatusSummary> equipmentStatuses;
    private List<WorkProgressInfo> workProgresses;
}
