package com.mes.messystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LotRequest {
    private Long productId;
    private Long workOrderId;
    private Integer quantity;
    private String remarks;
}
