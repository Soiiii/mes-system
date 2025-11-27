package com.mes.messystem.config;

import com.mes.messystem.domain.*;
import com.mes.messystem.repository.EquipmentRepository;
import com.mes.messystem.repository.InspectionStandardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final EquipmentRepository equipmentRepository;
    private final InspectionStandardRepository inspectionStandardRepository;

    @Override
    public void run(String... args) throws Exception {
        loadEquipmentData();
        loadInspectionStandardsData();
    }

    private void loadEquipmentData() {
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
            log.info("Equipment data already exists. Skipping equipment data load.");
        }
    }

    private void loadInspectionStandardsData() {
        // Check if inspection standards data already exists
        if (inspectionStandardRepository.count() == 0) {
            log.info("Loading sample inspection standards data...");

            // 치수 검사 기준
            InspectionStandard dimension1 = InspectionStandard.builder()
                    .code("DIM-001")
                    .name("Length")
                    .category("Dimension")
                    .standardValue("100.0")
                    .lowerLimit("99.5")
                    .upperLimit("100.5")
                    .unit("mm")
                    .applicableType(InspectionType.IN_PROCESS)
                    .description("Product length measurement")
                    .isActive(true)
                    .build();

            InspectionStandard dimension2 = InspectionStandard.builder()
                    .code("DIM-002")
                    .name("Width")
                    .category("Dimension")
                    .standardValue("50.0")
                    .lowerLimit("49.8")
                    .upperLimit("50.2")
                    .unit("mm")
                    .applicableType(InspectionType.IN_PROCESS)
                    .description("Product width measurement")
                    .isActive(true)
                    .build();

            InspectionStandard dimension3 = InspectionStandard.builder()
                    .code("DIM-003")
                    .name("Thickness")
                    .category("Dimension")
                    .standardValue("5.0")
                    .lowerLimit("4.9")
                    .upperLimit("5.1")
                    .unit("mm")
                    .applicableType(InspectionType.IN_PROCESS)
                    .description("Product thickness measurement")
                    .isActive(true)
                    .build();

            // 외관 검사 기준
            InspectionStandard appearance1 = InspectionStandard.builder()
                    .code("APP-001")
                    .name("Surface Roughness")
                    .category("Appearance")
                    .standardValue("1.6")
                    .lowerLimit("0.0")
                    .upperLimit("3.2")
                    .unit("Ra μm")
                    .applicableType(InspectionType.FINAL)
                    .description("Surface finish quality")
                    .isActive(true)
                    .build();

            InspectionStandard appearance2 = InspectionStandard.builder()
                    .code("APP-002")
                    .name("Color Consistency")
                    .category("Appearance")
                    .standardValue("Standard")
                    .lowerLimit("Pass")
                    .upperLimit("Pass")
                    .unit("-")
                    .applicableType(InspectionType.FINAL)
                    .description("Visual color inspection")
                    .isActive(true)
                    .build();

            // 성능 검사 기준
            InspectionStandard performance1 = InspectionStandard.builder()
                    .code("PERF-001")
                    .name("Tensile Strength")
                    .category("Performance")
                    .standardValue("500")
                    .lowerLimit("450")
                    .upperLimit("600")
                    .unit("MPa")
                    .applicableType(InspectionType.FINAL)
                    .description("Material strength test")
                    .isActive(true)
                    .build();

            InspectionStandard performance2 = InspectionStandard.builder()
                    .code("PERF-002")
                    .name("Operating Temperature")
                    .category("Performance")
                    .standardValue("25")
                    .lowerLimit("20")
                    .upperLimit("30")
                    .unit("°C")
                    .applicableType(InspectionType.OUTGOING)
                    .description("Normal operating temperature")
                    .isActive(true)
                    .build();

            // 수입 검사 기준
            InspectionStandard incoming1 = InspectionStandard.builder()
                    .code("INC-001")
                    .name("Material Purity")
                    .category("Material")
                    .standardValue("99.9")
                    .lowerLimit("99.5")
                    .upperLimit("100.0")
                    .unit("%")
                    .applicableType(InspectionType.INCOMING)
                    .description("Raw material purity check")
                    .isActive(true)
                    .build();

            InspectionStandard incoming2 = InspectionStandard.builder()
                    .code("INC-002")
                    .name("Moisture Content")
                    .category("Material")
                    .standardValue("0.5")
                    .lowerLimit("0.0")
                    .upperLimit("1.0")
                    .unit("%")
                    .applicableType(InspectionType.INCOMING)
                    .description("Material moisture level")
                    .isActive(true)
                    .build();

            inspectionStandardRepository.save(dimension1);
            inspectionStandardRepository.save(dimension2);
            inspectionStandardRepository.save(dimension3);
            inspectionStandardRepository.save(appearance1);
            inspectionStandardRepository.save(appearance2);
            inspectionStandardRepository.save(performance1);
            inspectionStandardRepository.save(performance2);
            inspectionStandardRepository.save(incoming1);
            inspectionStandardRepository.save(incoming2);

            log.info("Sample inspection standards data loaded successfully!");
        } else {
            log.info("Inspection standards data already exists. Skipping standards data load.");
        }
    }
}
