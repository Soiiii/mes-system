package com.mes.messystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDefectRate {
    private Long productId;
    private String productName;
    private int totalGoodQty;
    private int totalBadQty;
    private double defectRate;
}
