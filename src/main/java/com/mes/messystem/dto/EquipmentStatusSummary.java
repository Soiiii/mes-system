package com.mes.messystem.dto;

import com.mes.messystem.domain.EquipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentStatusSummary {
    private Long equipmentId;
    private String equipmentName;
    private String location;
    private EquipmentStatus status;
    private Double temperature;
    private Integer productionSpeed;
    private LocalDateTime lastUpdated;
}
