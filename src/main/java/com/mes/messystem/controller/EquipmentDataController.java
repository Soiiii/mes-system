package com.mes.messystem.controller;

import com.mes.messystem.domain.EquipmentData;
import com.mes.messystem.dto.EquipmentDataRequest;
import com.mes.messystem.service.EquipmentDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment-data")
@RequiredArgsConstructor
public class EquipmentDataController {

    private final EquipmentDataService equipmentDataService;

    @PostMapping
    public ResponseEntity<EquipmentData> receiveEquipmentData(@RequestBody EquipmentDataRequest request) {
        EquipmentData saved = equipmentDataService.saveEquipmentData(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{equipmentId}")
    public ResponseEntity<List<EquipmentData>> getEquipmentDataHistory(@PathVariable Long equipmentId) {
        List<EquipmentData> data = equipmentDataService.getEquipmentDataHistory(equipmentId);
        return ResponseEntity.ok(data);
    }
}
