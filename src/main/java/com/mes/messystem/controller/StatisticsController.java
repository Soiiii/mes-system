package com.mes.messystem.controller;

import com.mes.messystem.dto.ProductionStatistics;
import com.mes.messystem.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
@CrossOrigin(originPatterns = "*")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/production")
    public ResponseEntity<ProductionStatistics> getProductionStatistics() {
        return ResponseEntity.ok(statisticsService.getProductionStatistics());
    }
}
