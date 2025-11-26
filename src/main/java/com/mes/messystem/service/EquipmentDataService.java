package com.mes.messystem.service;

import com.mes.messystem.controller.WebSocketNotificationController;
import com.mes.messystem.domain.Equipment;
import com.mes.messystem.domain.EquipmentData;
import com.mes.messystem.domain.EquipmentStatus;
import com.mes.messystem.dto.EquipmentDataRequest;
import com.mes.messystem.dto.EquipmentStatusSummary;
import com.mes.messystem.repository.EquipmentDataRepository;
import com.mes.messystem.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EquipmentDataService {

    private final EquipmentDataRepository equipmentDataRepository;
    private final EquipmentRepository equipmentRepository;
    private final WebSocketNotificationController webSocketNotificationController;

    @Transactional
    public EquipmentData saveEquipmentData(EquipmentDataRequest request) {
        Equipment equipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        EquipmentData data = EquipmentData.builder()
                .equipment(equipment)
                .status(request.getStatus())
                .temperature(request.getTemperature())
                .productionSpeed(request.getProductionSpeed())
                .timestamp(LocalDateTime.now())
                .build();

        EquipmentData saved = equipmentDataRepository.save(data);

        log.info("Equipment Data Saved - Equipment: {}, Status: {}, Temp: {}°C, Speed: {}",
                equipment.getName(),
                saved.getStatus(),
                saved.getTemperature(),
                saved.getProductionSpeed());

        // Broadcast equipment status update via WebSocket
        EquipmentStatusSummary statusSummary = EquipmentStatusSummary.builder()
                .equipmentId(equipment.getId())
                .equipmentName(equipment.getName())
                .location(equipment.getLocation())
                .status(saved.getStatus())
                .temperature(saved.getTemperature())
                .productionSpeed(saved.getProductionSpeed())
                .lastUpdated(saved.getTimestamp())
                .build();

        webSocketNotificationController.broadcastEquipmentUpdate(statusSummary);

        // Send alert if equipment is in ALARM state
        if (saved.getStatus() == EquipmentStatus.ALARM) {
            webSocketNotificationController.broadcastAlert(
                    "Equipment " + equipment.getName() + " is in ALARM state!",
                    "WARNING"
            );
        }

        return saved;
    }

    public List<EquipmentData> getEquipmentDataHistory(Long equipmentId) {
        return equipmentDataRepository.findByEquipmentIdOrderByTimestampDesc(equipmentId);
    }
}
