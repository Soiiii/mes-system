package com.mes.messystem.dto;

import com.mes.messystem.domain.InspectionResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InspectionItemResponse {
    private Long id;
    private Long standardId;
    private String standardName;
    private String category;
    private String measuredValue;
    private String standardValue;
    private String upperLimit;
    private String lowerLimit;
    private String unit;
    private InspectionResult result;
    private String remarks;
}
