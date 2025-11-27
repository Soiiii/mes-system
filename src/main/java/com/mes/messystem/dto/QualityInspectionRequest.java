package com.mes.messystem.dto;

import com.mes.messystem.domain.InspectionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualityInspectionRequest {
    private Long lotId;
    private Long processId;
    private InspectionType type;
    private Integer sampleSize;
    private String inspector;
    private List<InspectionItemRequest> items;
    private String remarks;
}
