package com.mes.messystem.dto;

import com.mes.messystem.domain.LotStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LotResponse {
    private Long id;
    private String lotNumber;
    private Long productId;
    private String productName;
    private Long workOrderId;
    private Integer quantity;
    private LotStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String remarks;
    private Integer totalProcessed;  // 총 처리된 공정 수
    private Integer defectCount;     // 불량 수량
}
