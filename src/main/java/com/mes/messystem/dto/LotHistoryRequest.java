package com.mes.messystem.dto;

import com.mes.messystem.domain.ProcessResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LotHistoryRequest {
    private Long lotId;
    private Long processId;
    private Long equipmentId;
    private Integer inputQuantity;
    private Integer outputQuantity;
    private Integer defectQuantity;
    private ProcessResult result;
    private String operator;
    private String remarks;
}
