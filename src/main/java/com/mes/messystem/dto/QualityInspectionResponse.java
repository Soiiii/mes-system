package com.mes.messystem.dto;

import com.mes.messystem.domain.InspectionResult;
import com.mes.messystem.domain.InspectionStatus;
import com.mes.messystem.domain.InspectionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualityInspectionResponse {
    private Long id;
    private String inspectionNumber;
    private Long lotId;
    private String lotNumber;
    private Long processId;
    private String processName;
    private InspectionType type;
    private InspectionStatus status;
    private InspectionResult result;
    private Integer sampleSize;
    private Integer passedCount;
    private Integer failedCount;
    private String inspector;
    private LocalDateTime inspectionDate;
    private LocalDateTime createdAt;
    private List<InspectionItemResponse> items;
    private String remarks;
}
