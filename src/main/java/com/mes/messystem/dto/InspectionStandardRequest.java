package com.mes.messystem.dto;

import com.mes.messystem.domain.InspectionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InspectionStandardRequest {
    private String code;
    private String name;
    private String category;
    private String standardValue;
    private String upperLimit;
    private String lowerLimit;
    private String unit;
    private InspectionType applicableType;
    private Long productId;
    private String description;
    private Boolean isActive;
}
