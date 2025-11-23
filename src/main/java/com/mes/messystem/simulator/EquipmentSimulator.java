package com.mes.messystem.simulator;

import com.mes.messystem.domain.Equipment;
import com.mes.messystem.domain.EquipmentStatus;
import com.mes.messystem.dto.EquipmentDataRequest;
import com.mes.messystem.repository.EquipmentRepository;
import com.mes.messystem.service.EquipmentDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("simulator")
public class EquipmentSimulator {

    private final EquipmentDataService equipmentDataService;
    private final EquipmentRepository equipmentRepository;
    private final Random random = new Random();

    /**
     * Create and send data every 5 seconds
     */
    @Scheduled(fixedDelay = 5000)
    public void generateEquipmentData() {
        List<Equipment> equipments = equipmentRepository.findAll();

        if (equipments.isEmpty()) {
            log.warn("No equipment found in database. Simulator skipping...");
            return;
        }

        for (Equipment equipment : equipments) {
            EquipmentDataRequest request = EquipmentDataRequest.builder()
                    .equipmentId(equipment.getId())
                    .status(getRandomStatus())
                    .temperature(getRandomTemperature())
                    .productionSpeed(getRandomProductionSpeed())
                    .build();

            equipmentDataService.saveEquipmentData(request);
        }
    }

    /**
     * Create random equipment status (RUN / IDLE / ALARM)
     */
    private EquipmentStatus getRandomStatus() {
        EquipmentStatus[] statuses = EquipmentStatus.values();
        return statuses[random.nextInt(statuses.length)];
    }

    /**
     * Create random temperature (40~60)
     */
    private Double getRandomTemperature() {
        return 40.0 + (random.nextDouble() * 20.0);
    }

    /**
     * Create random production speed (100~150)
     */
    private Integer getRandomProductionSpeed() {
        return 100 + random.nextInt(51);
    }

    /**
     * 애플리케이션 시작 시 테스트용 설비 데이터 자동 생성
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeTestEquipment() {
        if (equipmentRepository.count() == 0) {
            log.info("Initializing test equipment...");

            Equipment cutting = Equipment.builder()
                    .name("Cutting Machine #1")
                    .location("Factory A - Line 1")
                    .type(com.mes.messystem.domain.EquipmentType.CUTTING_MACHINE)
                    .build();

            Equipment assembly = Equipment.builder()
                    .name("Assembly Machine #1")
                    .location("Factory A - Line 2")
                    .type(com.mes.messystem.domain.EquipmentType.ASSEMBLY_MACHINE)
                    .build();

            Equipment packaging = Equipment.builder()
                    .name("Packaging Machine #1")
                    .location("Factory A - Line 3")
                    .type(com.mes.messystem.domain.EquipmentType.PACKAGING_MACHINE)
                    .build();

            equipmentRepository.save(cutting);
            equipmentRepository.save(assembly);
            equipmentRepository.save(packaging);

            log.info("Test equipment initialized: {} machines", equipmentRepository.count());
        }
    }
}
