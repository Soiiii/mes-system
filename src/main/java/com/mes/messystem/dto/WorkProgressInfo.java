package com.mes.messystem.dto;

import com.mes.messystem.domain.WorkOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkProgressInfo {
    private Long workOrderId;
    private String productName;
    private WorkOrderStatus status;
    private int totalProcesses;
    private int completedProcesses;
    private double progressPercentage;
}
