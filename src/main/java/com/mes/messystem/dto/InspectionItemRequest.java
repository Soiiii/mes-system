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
public class InspectionItemRequest {
    private Long standardId;
    private String measuredValue;
    private InspectionResult result;
    private String remarks;
}
