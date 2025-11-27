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

    // Aliases for frontend compatibility
    public int getTodayProduction() {
        return totalQty;
    }

    public int getTodayDefects() {
        return totalBadQty;
    }

    public double getOperationRate() {
        return 100.0 - defectRate; // Simple calculation
    }

    public int getProductionCount() {
        return totalQty;
    }

    public int getDefectCount() {
        return totalBadQty;
    }
}
