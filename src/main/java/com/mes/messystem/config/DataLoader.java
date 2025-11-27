package com.mes.messystem.config;

import com.mes.messystem.domain.Equipment;
import com.mes.messystem.domain.EquipmentStatus;
import com.mes.messystem.domain.EquipmentType;
import com.mes.messystem.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final EquipmentRepository equipmentRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if equipment data already exists
        if (equipmentRepository.count() == 0) {
            log.info("Loading sample equipment data...");

            Equipment equipment1 = Equipment.builder()
                    .name("Cutting Machine 1")
                    .type(EquipmentType.CUTTING_MACHINE)
                    .status(EquipmentStatus.IDLE)
                    .location("Production Floor A")
                    .sequence(1)
                    .build();

            Equipment equipment2 = Equipment.builder()
                    .name("Cutting Machine 2")
                    .type(EquipmentType.CUTTING_MACHINE)
                    .status(EquipmentStatus.RUN)
                    .location("Production Floor A")
                    .sequence(2)
                    .build();

            Equipment equipment3 = Equipment.builder()
                    .name("Assembly Machine 1")
                    .type(EquipmentType.ASSEMBLY_MACHINE)
                    .status(EquipmentStatus.RUN)
                    .location("Production Floor B")
                    .sequence(3)
                    .build();

            Equipment equipment4 = Equipment.builder()
                    .name("Packaging Machine 1")
                    .type(EquipmentType.PACKAGING_MACHINE)
                    .status(EquipmentStatus.IDLE)
                    .location("Production Floor C")
                    .sequence(4)
                    .build();

            Equipment equipment5 = Equipment.builder()
                    .name("Inspection Machine 1")
                    .type(EquipmentType.INSPECTION_MACHINE)
                    .status(EquipmentStatus.RUN)
                    .location("QC Department")
                    .sequence(5)
                    .build();

            Equipment equipment6 = Equipment.builder()
                    .name("Assembly Machine 2")
                    .type(EquipmentType.ASSEMBLY_MACHINE)
                    .status(EquipmentStatus.IDLE)
                    .location("Production Floor B")
                    .sequence(6)
                    .build();

            equipmentRepository.save(equipment1);
            equipmentRepository.save(equipment2);
            equipmentRepository.save(equipment3);
            equipmentRepository.save(equipment4);
            equipmentRepository.save(equipment5);
            equipmentRepository.save(equipment6);

            log.info("Sample equipment data loaded successfully!");
        } else {
            log.info("Equipment data already exists. Skipping data load.");
        }
    }
}
