package com.mes.messystem.dto;

import com.mes.messystem.domain.ProcessResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LotHistoryResponse {
    private Long id;
    private Long lotId;
    private String lotNumber;
    private Long processId;
    private String processName;
    private Long equipmentId;
    private String equipmentName;
    private LocalDateTime processedAt;
    private Integer inputQuantity;
    private Integer outputQuantity;
    private Integer defectQuantity;
    private ProcessResult result;
    private String operator;
    private String remarks;
}
