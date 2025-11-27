package com.mes.messystem.controller;

import com.mes.messystem.dto.*;
import com.mes.messystem.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Get complete dashboard data
     */
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {
        DashboardResponse response = dashboardService.getDashboardData();
        return ResponseEntity.ok(response);
    }

    /**
     * Get today's production statistics
     */
    @GetMapping("/production/today")
    public ResponseEntity<TodayProductionStats> getTodayProduction() {
        TodayProductionStats stats = dashboardService.getTodayProductionStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Get defect rates by product
     */
    @GetMapping("/defect-rates")
    public ResponseEntity<List<ProductDefectRate>> getDefectRates() {
        List<ProductDefectRate> rates = dashboardService.getProductDefectRates();
        return ResponseEntity.ok(rates);
    }

    /**
     * Get equipment statuses
     */
    @GetMapping("/equipment/statuses")
    public ResponseEntity<List<EquipmentStatusSummary>> getEquipmentStatuses() {
        List<EquipmentStatusSummary> statuses = dashboardService.getEquipmentStatuses();
        return ResponseEntity.ok(statuses);
    }

    /**
     * Get work order progress
     */
    @GetMapping("/work/progress")
    public ResponseEntity<List<WorkProgressInfo>> getWorkProgress() {
        List<WorkProgressInfo> progress = dashboardService.getWorkProgresses();
        return ResponseEntity.ok(progress);
    }

    /**
     * Get dashboard stats (for frontend)
     */
    @GetMapping("/stats")
    public ResponseEntity<TodayProductionStats> getStats() {
        TodayProductionStats stats = dashboardService.getTodayProductionStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Get defect rate (for frontend)
     */
    @GetMapping("/defect-rate")
    public ResponseEntity<java.util.Map<String, Object>> getDefectRate() {
        TodayProductionStats stats = dashboardService.getTodayProductionStats();
        int goodCount = stats.getProductionCount() - stats.getDefectCount();
        return ResponseEntity.ok(java.util.Map.of(
                "goodCount", goodCount,
                "defectCount", stats.getDefectCount()
        ));
    }

    /**
     * SSE endpoint for real-time dashboard updates
     * Updates every 3 seconds
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamDashboard(@RequestParam(defaultValue = "3000") long interval) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);

        log.info("New SSE connection established. Total connections: {}", emitters.size());

        emitter.onCompletion(() -> {
            emitters.remove(emitter);
            log.info("SSE connection completed. Remaining connections: {}", emitters.size());
        });

        emitter.onTimeout(() -> {
            emitters.remove(emitter);
            log.info("SSE connection timed out. Remaining connections: {}", emitters.size());
        });

        emitter.onError(e -> {
            emitters.remove(emitter);
            log.error("SSE connection error. Remaining connections: {}", emitters.size(), e);
        });

        // Send initial data
        try {
            DashboardResponse initialData = dashboardService.getDashboardData();
            emitter.send(SseEmitter.event()
                    .name("dashboard-update")
                    .data(initialData));
        } catch (IOException e) {
            log.error("Error sending initial SSE data", e);
            emitter.completeWithError(e);
        }

        // Schedule periodic updates every 3 seconds
        scheduler.scheduleAtFixedRate(() -> {
            try {
                DashboardResponse data = dashboardService.getDashboardData();
                emitter.send(SseEmitter.event()
                        .name("dashboard-update")
                        .data(data));
            } catch (IOException e) {
                log.error("Error sending SSE update", e);
                emitter.completeWithError(e);
            }
        }, interval, interval, TimeUnit.MILLISECONDS);

        return emitter;
    }

    /**
     * Broadcast dashboard update to all connected clients
     */
    public void broadcastDashboardUpdate() {
        DashboardResponse data = dashboardService.getDashboardData();

        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();

        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("dashboard-update")
                        .data(data));
            } catch (IOException e) {
                deadEmitters.add(emitter);
                log.warn("Failed to send to emitter, marking for removal", e);
            }
        });

        emitters.removeAll(deadEmitters);
        log.debug("Broadcasted dashboard update to {} clients", emitters.size());
    }
}
