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
public class LotService {

    private final LotRepository lotRepository;
    private final LotHistoryRepository lotHistoryRepository;
    private final ProductRepository productRepository;
    private final WorkOrderRepository workOrderRepository;
    private final ProcessRepository processRepository;
    private final EquipmentRepository equipmentRepository;

    /**
     * LOT 번호 생성: LOT-YYYYMMDD-XXXX
     */
    private String generateLotNumber() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        
        Long count = lotRepository.countByCreatedAtAfter(startOfDay);
        String sequence = String.format("%04d", count + 1);
        
        return "LOT-" + today + "-" + sequence;
    }

    /**
     * 새로운 LOT 생성
     */
    @Transactional
    public LotResponse createLot(LotRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        WorkOrder workOrder = workOrderRepository.findById(request.getWorkOrderId())
                .orElseThrow(() -> new RuntimeException("Work Order not found"));

        Lot lot = Lot.builder()
                .lotNumber(generateLotNumber())
                .product(product)
                .workOrder(workOrder)
                .quantity(request.getQuantity())
                .status(LotStatus.CREATED)
                .remarks(request.getRemarks())
                .build();

        lot = lotRepository.save(lot);
        log.info("Created new LOT: {}", lot.getLotNumber());

        return convertToResponse(lot);
    }

    /**
     * LOT 목록 조회
     */
    public List<LotResponse> getAllLots() {
        return lotRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * LOT 상세 조회
     */
    public LotResponse getLotById(Long id) {
        Lot lot = lotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lot not found"));
        return convertToResponse(lot);
    }

    /**
     * LOT 번호로 조회
     */
    public LotResponse getLotByLotNumber(String lotNumber) {
        Lot lot = lotRepository.findByLotNumber(lotNumber)
                .orElseThrow(() -> new RuntimeException("Lot not found: " + lotNumber));
        return convertToResponse(lot);
    }

    /**
     * LOT 상태 변경
     */
    @Transactional
    public LotResponse updateLotStatus(Long id, LotStatus status) {
        Lot lot = lotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lot not found"));

        lot.setStatus(status);

        if (status == LotStatus.IN_PROGRESS && lot.getStartedAt() == null) {
            lot.setStartedAt(LocalDateTime.now());
        } else if (status == LotStatus.COMPLETED && lot.getCompletedAt() == null) {
            lot.setCompletedAt(LocalDateTime.now());
        }

        lot = lotRepository.save(lot);
        log.info("Updated LOT status: {} -> {}", lot.getLotNumber(), status);

        return convertToResponse(lot);
    }

    /**
     * LOT 이력 추가 (공정 처리)
     */
    @Transactional
    public LotHistoryResponse addLotHistory(LotHistoryRequest request) {
        Lot lot = lotRepository.findById(request.getLotId())
                .orElseThrow(() -> new RuntimeException("Lot not found"));

        ProcessEntity process = processRepository.findById(request.getProcessId())
                .orElseThrow(() -> new RuntimeException("Process not found"));

        Equipment equipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        LotHistory history = LotHistory.builder()
                .lot(lot)
                .process(process)
                .equipment(equipment)
                .inputQuantity(request.getInputQuantity())
                .outputQuantity(request.getOutputQuantity())
                .defectQuantity(request.getDefectQuantity())
                .result(request.getResult())
                .operator(request.getOperator())
                .remarks(request.getRemarks())
                .build();

        history = lotHistoryRepository.save(history);

        // LOT 상태 자동 업데이트
        if (lot.getStatus() == LotStatus.CREATED) {
            lot.setStatus(LotStatus.IN_PROGRESS);
            lot.setStartedAt(LocalDateTime.now());
            lotRepository.save(lot);
        }

        log.info("Added history to LOT: {} - Process: {}", lot.getLotNumber(), process.getName());

        return convertToHistoryResponse(history);
    }

    /**
     * LOT의 전체 이력 조회 (추적)
     */
    public List<LotHistoryResponse> getLotHistory(Long lotId) {
        return lotHistoryRepository.findByLotIdOrderByProcessedAtAsc(lotId).stream()
                .map(this::convertToHistoryResponse)
                .collect(Collectors.toList());
    }

    /**
     * LOT 번호로 이력 조회
     */
    public List<LotHistoryResponse> getLotHistoryByLotNumber(String lotNumber) {
        return lotHistoryRepository.findByLotNumberOrderByProcessedAt(lotNumber).stream()
                .map(this::convertToHistoryResponse)
                .collect(Collectors.toList());
    }

    /**
     * 제품별 LOT 조회
     */
    public List<LotResponse> getLotsByProduct(Long productId) {
        return lotRepository.findByProductId(productId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 작업 오더별 LOT 조회
     */
    public List<LotResponse> getLotsByWorkOrder(Long workOrderId) {
        return lotRepository.findByWorkOrderId(workOrderId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * LOT 검색
     */
    public List<LotResponse> searchLots(String keyword) {
        return lotRepository.searchByKeyword(keyword).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Entity -> Response 변환
     */
    private LotResponse convertToResponse(Lot lot) {
        List<LotHistory> histories = lotHistoryRepository.findByLotId(lot.getId());
        
        int totalDefects = histories.stream()
                .mapToInt(h -> h.getDefectQuantity() != null ? h.getDefectQuantity() : 0)
                .sum();

        return LotResponse.builder()
                .id(lot.getId())
                .lotNumber(lot.getLotNumber())
                .productId(lot.getProduct().getId())
                .productName(lot.getProduct().getName())
                .workOrderId(lot.getWorkOrder() != null ? lot.getWorkOrder().getId() : null)
                .quantity(lot.getQuantity())
                .status(lot.getStatus())
                .createdAt(lot.getCreatedAt())
                .startedAt(lot.getStartedAt())
                .completedAt(lot.getCompletedAt())
                .remarks(lot.getRemarks())
                .totalProcessed(histories.size())
                .defectCount(totalDefects)
                .build();
    }

    /**
     * LotHistory Entity -> Response 변환
     */
    private LotHistoryResponse convertToHistoryResponse(LotHistory history) {
        return LotHistoryResponse.builder()
                .id(history.getId())
                .lotId(history.getLot().getId())
                .lotNumber(history.getLot().getLotNumber())
                .processId(history.getProcess().getId())
                .processName(history.getProcess().getName())
                .equipmentId(history.getEquipment() != null ? history.getEquipment().getId() : null)
                .equipmentName(history.getEquipment() != null ? history.getEquipment().getName() : null)
                .processedAt(history.getProcessedAt())
                .inputQuantity(history.getInputQuantity())
                .outputQuantity(history.getOutputQuantity())
                .defectQuantity(history.getDefectQuantity())
                .result(history.getResult())
                .operator(history.getOperator())
                .remarks(history.getRemarks())
                .build();
    }
}
