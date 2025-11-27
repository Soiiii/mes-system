package com.mes.messystem.service;

import com.mes.messystem.domain.*;
import com.mes.messystem.dto.*;
import com.mes.messystem.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QualityInspectionService {

    private final QualityInspectionRepository inspectionRepository;
    private final InspectionStandardRepository standardRepository;
    private final InspectionItemRepository itemRepository;
    private final LotRepository lotRepository;
    private final ProcessRepository processRepository;

    /**
     * 검사 번호 생성: INS-YYYYMMDD-XXXX
     */
    private String generateInspectionNumber() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        
        Long count = inspectionRepository.countByCreatedAtAfter(startOfDay);
        String sequence = String.format("%04d", count + 1);
        
        return "INS-" + today + "-" + sequence;
    }

    /**
     * 품질 검사 생성
     */
    @Transactional
    public QualityInspectionResponse createInspection(QualityInspectionRequest request) {
        Lot lot = lotRepository.findById(request.getLotId())
                .orElseThrow(() -> new RuntimeException("Lot not found"));

        ProcessEntity process = null;
        if (request.getProcessId() != null) {
            process = processRepository.findById(request.getProcessId())
                    .orElseThrow(() -> new RuntimeException("Process not found"));
        }

        QualityInspection inspection = QualityInspection.builder()
                .inspectionNumber(generateInspectionNumber())
                .lot(lot)
                .process(process)
                .type(request.getType())
                .sampleSize(request.getSampleSize())
                .inspector(request.getInspector())
                .status(InspectionStatus.PENDING)
                .remarks(request.getRemarks())
                .build();

        inspection = inspectionRepository.save(inspection);

        // 검사 항목 추가
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (InspectionItemRequest itemRequest : request.getItems()) {
                InspectionStandard standard = standardRepository.findById(itemRequest.getStandardId())
                        .orElseThrow(() -> new RuntimeException("Standard not found"));

                InspectionItem item = InspectionItem.builder()
                        .inspection(inspection)
                        .standard(standard)
                        .measuredValue(itemRequest.getMeasuredValue())
                        .standardValue(standard.getStandardValue())
                        .tolerance(standard.getUpperLimit() + "~" + standard.getLowerLimit())
                        .result(itemRequest.getResult())
                        .remarks(itemRequest.getRemarks())
                        .build();

                itemRepository.save(item);
                inspection.getItems().add(item);
            }
        }

        log.info("Created quality inspection: {}", inspection.getInspectionNumber());
        return convertToResponse(inspection);
    }

    /**
     * 검사 완료 처리
     */
    @Transactional
    public QualityInspectionResponse completeInspection(Long id, InspectionResult result) {
        QualityInspection inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inspection not found"));

        inspection.setStatus(InspectionStatus.COMPLETED);
        inspection.setResult(result);
        inspection.setInspectionDate(LocalDateTime.now());

        // 합격/불합격 수 계산
        List<InspectionItem> items = inspection.getItems();
        int passedCount = (int) items.stream()
                .filter(item -> item.getResult() == InspectionResult.PASS)
                .count();
        int failedCount = (int) items.stream()
                .filter(item -> item.getResult() == InspectionResult.FAIL)
                .count();

        inspection.setPassedCount(passedCount);
        inspection.setFailedCount(failedCount);

        inspection = inspectionRepository.save(inspection);
        log.info("Completed inspection: {} - Result: {}", inspection.getInspectionNumber(), result);

        return convertToResponse(inspection);
    }

    /**
     * 모든 검사 조회
     */
    public List<QualityInspectionResponse> getAllInspections() {
        return inspectionRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 검사 상세 조회
     */
    public QualityInspectionResponse getInspectionById(Long id) {
        QualityInspection inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inspection not found"));
        return convertToResponse(inspection);
    }

    /**
     * LOT별 검사 이력 조회
     */
    public List<QualityInspectionResponse> getInspectionsByLot(Long lotId) {
        return inspectionRepository.findByLotId(lotId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 검사 유형별 조회
     */
    public List<QualityInspectionResponse> getInspectionsByType(InspectionType type) {
        return inspectionRepository.findByType(type).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 검사 결과별 조회
     */
    public List<QualityInspectionResponse> getInspectionsByResult(InspectionResult result) {
        return inspectionRepository.findByResult(result).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 검사 기준 생성
     */
    @Transactional
    public InspectionStandard createStandard(InspectionStandardRequest request) {
        Product product = null;
        if (request.getProductId() != null) {
            product = lotRepository.findById(request.getProductId())
                    .map(Lot::getProduct)
                    .orElse(null);
        }

        InspectionStandard standard = InspectionStandard.builder()
                .code(request.getCode())
                .name(request.getName())
                .category(request.getCategory())
                .standardValue(request.getStandardValue())
                .upperLimit(request.getUpperLimit())
                .lowerLimit(request.getLowerLimit())
                .unit(request.getUnit())
                .applicableType(request.getApplicableType())
                .product(product)
                .description(request.getDescription())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        return standardRepository.save(standard);
    }

    /**
     * 검사 기준 목록 조회
     */
    public List<InspectionStandard> getAllStandards() {
        return standardRepository.findAll();
    }

    /**
     * 제품별 검사 기준 조회
     */
    public List<InspectionStandard> getStandardsByProduct(Long productId, InspectionType type) {
        if (type != null) {
            return standardRepository.findByProductIdAndApplicableType(productId, type);
        }
        return standardRepository.findByProductId(productId);
    }

    /**
     * Entity -> Response 변환
     */
    private QualityInspectionResponse convertToResponse(QualityInspection inspection) {
        List<InspectionItemResponse> itemResponses = inspection.getItems().stream()
                .map(this::convertItemToResponse)
                .collect(Collectors.toList());

        return QualityInspectionResponse.builder()
                .id(inspection.getId())
                .inspectionNumber(inspection.getInspectionNumber())
                .lotId(inspection.getLot().getId())
                .lotNumber(inspection.getLot().getLotNumber())
                .processId(inspection.getProcess() != null ? inspection.getProcess().getId() : null)
                .processName(inspection.getProcess() != null ? inspection.getProcess().getName() : null)
                .type(inspection.getType())
                .status(inspection.getStatus())
                .result(inspection.getResult())
                .sampleSize(inspection.getSampleSize())
                .passedCount(inspection.getPassedCount())
                .failedCount(inspection.getFailedCount())
                .inspector(inspection.getInspector())
                .inspectionDate(inspection.getInspectionDate())
                .createdAt(inspection.getCreatedAt())
                .items(itemResponses)
                .remarks(inspection.getRemarks())
                .build();
    }

    private InspectionItemResponse convertItemToResponse(InspectionItem item) {
        InspectionStandard standard = item.getStandard();
        
        return InspectionItemResponse.builder()
                .id(item.getId())
                .standardId(standard.getId())
                .standardName(standard.getName())
                .category(standard.getCategory())
                .measuredValue(item.getMeasuredValue())
                .standardValue(standard.getStandardValue())
                .upperLimit(standard.getUpperLimit())
                .lowerLimit(standard.getLowerLimit())
                .unit(standard.getUnit())
                .result(item.getResult())
                .remarks(item.getRemarks())
                .build();
    }
}
