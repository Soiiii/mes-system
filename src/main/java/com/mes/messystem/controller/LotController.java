package com.mes.messystem.controller;

import com.mes.messystem.domain.LotStatus;
import com.mes.messystem.dto.*;
import com.mes.messystem.service.LotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lots")
@CrossOrigin(originPatterns = "*")
public class LotController {

    private final LotService lotService;

    /**
     * 모든 LOT 조회
     */
    @GetMapping
    public List<LotResponse> getAllLots() {
        return lotService.getAllLots();
    }

    /**
     * LOT 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<LotResponse> getLotById(@PathVariable Long id) {
        return ResponseEntity.ok(lotService.getLotById(id));
    }

    /**
     * LOT 번호로 조회
     */
    @GetMapping("/number/{lotNumber}")
    public ResponseEntity<LotResponse> getLotByLotNumber(@PathVariable String lotNumber) {
        return ResponseEntity.ok(lotService.getLotByLotNumber(lotNumber));
    }

    /**
     * 새 LOT 생성
     */
    @PostMapping
    public ResponseEntity<LotResponse> createLot(@RequestBody LotRequest request) {
        return ResponseEntity.ok(lotService.createLot(request));
    }

    /**
     * LOT 상태 변경
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<LotResponse> updateLotStatus(
            @PathVariable Long id,
            @RequestParam LotStatus status) {
        return ResponseEntity.ok(lotService.updateLotStatus(id, status));
    }

    /**
     * LOT 이력 추가 (공정 처리)
     */
    @PostMapping("/history")
    public ResponseEntity<LotHistoryResponse> addLotHistory(@RequestBody LotHistoryRequest request) {
        return ResponseEntity.ok(lotService.addLotHistory(request));
    }

    /**
     * LOT의 전체 이력 조회
     */
    @GetMapping("/{id}/history")
    public List<LotHistoryResponse> getLotHistory(@PathVariable Long id) {
        return lotService.getLotHistory(id);
    }

    /**
     * LOT 번호로 이력 조회
     */
    @GetMapping("/number/{lotNumber}/history")
    public List<LotHistoryResponse> getLotHistoryByLotNumber(@PathVariable String lotNumber) {
        return lotService.getLotHistoryByLotNumber(lotNumber);
    }

    /**
     * 제품별 LOT 조회
     */
    @GetMapping("/product/{productId}")
    public List<LotResponse> getLotsByProduct(@PathVariable Long productId) {
        return lotService.getLotsByProduct(productId);
    }

    /**
     * 작업 오더별 LOT 조회
     */
    @GetMapping("/work-order/{workOrderId}")
    public List<LotResponse> getLotsByWorkOrder(@PathVariable Long workOrderId) {
        return lotService.getLotsByWorkOrder(workOrderId);
    }

    /**
     * LOT 검색
     */
    @GetMapping("/search")
    public List<LotResponse> searchLots(@RequestParam String keyword) {
        return lotService.searchLots(keyword);
    }
}
