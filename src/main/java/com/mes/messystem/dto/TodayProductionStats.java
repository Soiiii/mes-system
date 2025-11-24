package com.mes.messystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodayProductionStats {
    private int totalGoodQty;
    private int totalBadQty;
    private int totalQty;
    private double defectRate;
}
