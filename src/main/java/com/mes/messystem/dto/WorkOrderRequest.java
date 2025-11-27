package com.mes.messystem.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkOrderRequest {
    private Long productId;
    private Integer quantity;
    private String plannedStartDate;
    private String plannedEndDate;
}