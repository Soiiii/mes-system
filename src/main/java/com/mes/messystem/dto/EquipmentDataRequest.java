package com.mes.messystem.dto;

import com.mes.messystem.domain.EquipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentDataRequest {
    private Long equipmentId;
    private EquipmentStatus status;
    private Double temperature;
    private Integer productionSpeed;
}
