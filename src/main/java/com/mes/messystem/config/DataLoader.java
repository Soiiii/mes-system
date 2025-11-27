package com.mes.messystem.config;

import com.mes.messystem.domain.*;
import com.mes.messystem.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final EquipmentRepository equipmentRepository;
    private final InspectionStandardRepository inspectionStandardRepository;
    private final ProductRepository productRepository;
    private final ProcessRepository processRepository;
    private final WorkOrderRepository workOrderRepository;
    private final LotRepository lotRepository;
    private final LotHistoryRepository lotHistoryRepository;
    private final QualityInspectionRepository qualityInspectionRepository;
    private final InspectionItemRepository inspectionItemRepository;

    @Override
    public void run(String... args) throws Exception {
        loadEquipmentData();
        loadInspectionStandardsData();
        loadCompleteFlowData();
    }

    private void loadEquipmentData() {
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
        }
    }

    private void loadInspectionStandardsData() {
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
        }
    }

    private void loadCompleteFlowData() {
        if (productRepository.count() == 0) {
            log.info("Loading complete flow sample data...");

            // 1. Products 생성
            Product product1 = Product.builder()
                    .name("Car Seat Frame")
                    .code("PROD-001")
                    .description("Automotive seat frame structure")
                    .processes(new ArrayList<>())
                    .build();

            Product product2 = Product.builder()
                    .name("Dashboard Panel")
                    .code("PROD-002")
                    .description("Vehicle dashboard plastic panel")
                    .processes(new ArrayList<>())
                    .build();

            productRepository.save(product1);
            productRepository.save(product2);

            // 2. Processes 생성
            ProcessEntity process1 = ProcessEntity.builder()
                    .name("Cutting")
                    .code("PROC-001")
                    .description("Material cutting process")
                    .sequence(1)
                    .build();

            ProcessEntity process2 = ProcessEntity.builder()
                    .name("Welding")
                    .code("PROC-002")
                    .description("Frame welding process")
                    .sequence(2)
                    .build();

            ProcessEntity process3 = ProcessEntity.builder()
                    .name("Assembly")
                    .code("PROC-003")
                    .description("Component assembly process")
                    .sequence(3)
                    .build();

            ProcessEntity process4 = ProcessEntity.builder()
                    .name("Quality Inspection")
                    .code("PROC-004")
                    .description("Final quality check")
                    .sequence(4)
                    .build();

            processRepository.save(process1);
            processRepository.save(process2);
            processRepository.save(process3);
            processRepository.save(process4);

            // Product에 Process 연결
            product1.addProcess(process1);
            product1.addProcess(process2);
            product1.addProcess(process3);
            product1.addProcess(process4);

            product2.addProcess(process1);
            product2.addProcess(process3);
            product2.addProcess(process4);

            productRepository.save(product1);
            productRepository.save(product2);

            // 3. Work Orders 생성
            WorkOrder workOrder1 = WorkOrder.builder()
                    .product(product1)
                    .quantity(100)
                    .status(WorkOrderStatus.IN_PROGRESS)
                    .startTime(LocalDateTime.now().minusDays(2))
                    .build();

            WorkOrder workOrder2 = WorkOrder.builder()
                    .product(product2)
                    .quantity(50)
                    .status(WorkOrderStatus.COMPLETED)
                    .startTime(LocalDateTime.now().minusDays(5))
                    .finishTime(LocalDateTime.now().minusDays(1))
                    .build();

            workOrderRepository.save(workOrder1);
            workOrderRepository.save(workOrder2);

            // 4. LOTs 생성
            Lot lot1 = createLot(product1, workOrder1, 100, 0);
            Lot lot2 = createLot(product1, workOrder1, 100, 1);
            Lot lot3 = createLot(product2, workOrder2, 50, 0);

            // 5. LOT 공정 이력 생성
            List<Equipment> equipments = equipmentRepository.findAll();
            
            // LOT 1 - 완전한 공정 처리 (모든 공정 완료)
            createLotHistory(lot1, process1, equipments.get(0), 100, 98, 2, ProcessResult.PASS, "John Doe");
            createLotHistory(lot1, process2, equipments.get(2), 98, 96, 2, ProcessResult.PASS, "Jane Smith");
            createLotHistory(lot1, process3, equipments.get(2), 96, 95, 1, ProcessResult.PASS, "John Doe");
            createLotHistory(lot1, process4, equipments.get(4), 95, 95, 0, ProcessResult.PASS, "QC Inspector");
            lot1.setStatus(LotStatus.COMPLETED);
            lot1.setStartedAt(LocalDateTime.now().minusDays(2));
            lot1.setCompletedAt(LocalDateTime.now().minusHours(6));
            lotRepository.save(lot1);

            // LOT 2 - 진행 중 (일부 공정만 완료)
            createLotHistory(lot2, process1, equipments.get(1), 100, 99, 1, ProcessResult.PASS, "Mike Johnson");
            createLotHistory(lot2, process2, equipments.get(2), 99, 98, 1, ProcessResult.PASS, "Jane Smith");
            lot2.setStatus(LotStatus.IN_PROGRESS);
            lot2.setStartedAt(LocalDateTime.now().minusDays(1));
            lotRepository.save(lot2);

            // LOT 3 - 완료됨
            createLotHistory(lot3, process1, equipments.get(0), 50, 50, 0, ProcessResult.PASS, "John Doe");
            createLotHistory(lot3, process3, equipments.get(5), 50, 49, 1, ProcessResult.PASS, "Mike Johnson");
            createLotHistory(lot3, process4, equipments.get(4), 49, 49, 0, ProcessResult.PASS, "QC Inspector");
            lot3.setStatus(LotStatus.COMPLETED);
            lot3.setStartedAt(LocalDateTime.now().minusDays(5));
            lot3.setCompletedAt(LocalDateTime.now().minusDays(1));
            lotRepository.save(lot3);

            // 6. Quality Inspections 생성
            List<InspectionStandard> standards = inspectionStandardRepository.findAll();

            // LOT 1 - 공정 중 검사 (COMPLETED, PASS)
            QualityInspection inspection1 = createInspection(
                lot1, process2, InspectionType.IN_PROCESS, 10, "QC Team A"
            );
            addInspectionItems(inspection1, standards, InspectionType.IN_PROCESS, true);
            inspection1.setStatus(InspectionStatus.COMPLETED);
            inspection1.setResult(InspectionResult.PASS);
            inspection1.setInspectionDate(LocalDateTime.now().minusDays(1));
            inspection1.setPassedCount(3);
            inspection1.setFailedCount(0);
            qualityInspectionRepository.save(inspection1);

            // LOT 1 - 최종 검사 (COMPLETED, PASS)
            QualityInspection inspection2 = createInspection(
                lot1, process4, InspectionType.FINAL, 5, "QC Team B"
            );
            addInspectionItems(inspection2, standards, InspectionType.FINAL, true);
            inspection2.setStatus(InspectionStatus.COMPLETED);
            inspection2.setResult(InspectionResult.PASS);
            inspection2.setInspectionDate(LocalDateTime.now().minusHours(6));
            inspection2.setPassedCount(3);
            inspection2.setFailedCount(0);
            qualityInspectionRepository.save(inspection2);

            // LOT 2 - 공정 중 검사 (PENDING)
            QualityInspection inspection3 = createInspection(
                lot2, process2, InspectionType.IN_PROCESS, 8, "QC Team A"
            );
            addInspectionItems(inspection3, standards, InspectionType.IN_PROCESS, false);
            qualityInspectionRepository.save(inspection3);

            // LOT 3 - 최종 검사 (COMPLETED, CONDITIONAL_PASS)
            QualityInspection inspection4 = createInspection(
                lot3, process4, InspectionType.FINAL, 5, "QC Team B"
            );
            addInspectionItems(inspection4, standards, InspectionType.FINAL, false);
            inspection4.setStatus(InspectionStatus.COMPLETED);
            inspection4.setResult(InspectionResult.CONDITIONAL_PASS);
            inspection4.setInspectionDate(LocalDateTime.now().minusDays(1));
            inspection4.setPassedCount(2);
            inspection4.setFailedCount(1);
            qualityInspectionRepository.save(inspection4);

            log.info("Complete flow sample data loaded successfully!");
            log.info("Created: 2 Products, 4 Processes, 2 Work Orders, 3 LOTs, 4 Quality Inspections");
        }
    }

    private Lot createLot(Product product, WorkOrder workOrder, int quantity, int sequence) {
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String lotNumber = String.format("LOT-%s-%04d", today, sequence + 1);

        Lot lot = Lot.builder()
                .lotNumber(lotNumber)
                .product(product)
                .workOrder(workOrder)
                .quantity(quantity)
                .status(LotStatus.CREATED)
                .histories(new ArrayList<>())
                .build();

        return lotRepository.save(lot);
    }

    private void createLotHistory(Lot lot, ProcessEntity process, Equipment equipment,
                                   int inputQty, int outputQty, int defectQty,
                                   ProcessResult result, String operator) {
        LotHistory history = LotHistory.builder()
                .lot(lot)
                .process(process)
                .equipment(equipment)
                .inputQuantity(inputQty)
                .outputQuantity(outputQty)
                .defectQuantity(defectQty)
                .result(result)
                .operator(operator)
                .remarks("Sample production run")
                .build();

        lotHistoryRepository.save(history);
        lot.getHistories().add(history);
    }

    private QualityInspection createInspection(Lot lot, ProcessEntity process,
                                                InspectionType type, int sampleSize,
                                                String inspector) {
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = qualityInspectionRepository.count();
        String inspectionNumber = String.format("INS-%s-%04d", today, count + 1);

        QualityInspection inspection = QualityInspection.builder()
                .inspectionNumber(inspectionNumber)
                .lot(lot)
                .process(process)
                .type(type)
                .sampleSize(sampleSize)
                .inspector(inspector)
                .status(InspectionStatus.PENDING)
                .items(new ArrayList<>())
                .build();

        return qualityInspectionRepository.save(inspection);
    }

    private void addInspectionItems(QualityInspection inspection,
                                     List<InspectionStandard> allStandards,
                                     InspectionType type,
                                     boolean withMeasurements) {
        List<InspectionStandard> applicableStandards = allStandards.stream()
                .filter(s -> s.getApplicableType() == type)
                .toList();

        for (InspectionStandard standard : applicableStandards) {
            String measuredValue = "";
            InspectionResult result = InspectionResult.PASS;

            if (withMeasurements) {
                // 측정값 시뮬레이션
                double standardVal = Double.parseDouble(standard.getStandardValue());
                double lowerLimit = Double.parseDouble(standard.getLowerLimit());
                double upperLimit = Double.parseDouble(standard.getUpperLimit());
                double range = upperLimit - lowerLimit;
                
                // 대부분 PASS가 되도록 범위 내에서 랜덤 생성
                double randomValue = lowerLimit + (range * 0.3) + (Math.random() * range * 0.4);
                measuredValue = String.format("%.1f", randomValue);
                
                result = InspectionResult.PASS;
            }

            InspectionItem item = InspectionItem.builder()
                    .inspection(inspection)
                    .standard(standard)
                    .measuredValue(measuredValue)
                    .standardValue(standard.getStandardValue())
                    .tolerance(standard.getUpperLimit() + "~" + standard.getLowerLimit())
                    .result(result)
                    .build();

            inspectionItemRepository.save(item);
            inspection.getItems().add(item);
        }
    }
}
