package com.mes.messystem.controller;

import com.mes.messystem.domain.InspectionResult;
import com.mes.messystem.domain.InspectionStandard;
import com.mes.messystem.domain.InspectionType;
import com.mes.messystem.dto.InspectionStandardRequest;
import com.mes.messystem.dto.QualityInspectionRequest;
import com.mes.messystem.dto.QualityInspectionResponse;
import com.mes.messystem.service.QualityInspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quality-inspections")
@CrossOrigin(originPatterns = "*")
public class QualityInspectionController {

    private final QualityInspectionService inspectionService;

    /**
     * 모든 검사 조회
     */
    @GetMapping
    public List<QualityInspectionResponse> getAllInspections() {
        return inspectionService.getAllInspections();
    }

    /**
     * 검사 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<QualityInspectionResponse> getInspectionById(@PathVariable Long id) {
        return ResponseEntity.ok(inspectionService.getInspectionById(id));
    }

    /**
     * 새 검사 생성
     */
    @PostMapping
    public ResponseEntity<QualityInspectionResponse> createInspection(
            @RequestBody QualityInspectionRequest request) {
        return ResponseEntity.ok(inspectionService.createInspection(request));
    }

    /**
     * 검사 완료 처리
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<QualityInspectionResponse> completeInspection(
            @PathVariable Long id,
            @RequestParam InspectionResult result) {
        return ResponseEntity.ok(inspectionService.completeInspection(id, result));
    }

    /**
     * LOT별 검사 이력 조회
     */
    @GetMapping("/lot/{lotId}")
    public List<QualityInspectionResponse> getInspectionsByLot(@PathVariable Long lotId) {
        return inspectionService.getInspectionsByLot(lotId);
    }

    /**
     * 검사 유형별 조회
     */
    @GetMapping("/type/{type}")
    public List<QualityInspectionResponse> getInspectionsByType(@PathVariable InspectionType type) {
        return inspectionService.getInspectionsByType(type);
    }

    /**
     * 검사 결과별 조회
     */
    @GetMapping("/result/{result}")
    public List<QualityInspectionResponse> getInspectionsByResult(@PathVariable InspectionResult result) {
        return inspectionService.getInspectionsByResult(result);
    }

    /**
     * 검사 기준 생성
     */
    @PostMapping("/standards")
    public ResponseEntity<InspectionStandard> createStandard(
            @RequestBody InspectionStandardRequest request) {
        return ResponseEntity.ok(inspectionService.createStandard(request));
    }

    /**
     * 모든 검사 기준 조회
     */
    @GetMapping("/standards")
    public List<InspectionStandard> getAllStandards() {
        return inspectionService.getAllStandards();
    }

    /**
     * 제품별 검사 기준 조회
     */
    @GetMapping("/standards/product/{productId}")
    public List<InspectionStandard> getStandardsByProduct(
            @PathVariable Long productId,
            @RequestParam(required = false) InspectionType type) {
        return inspectionService.getStandardsByProduct(productId, type);
    }
}
