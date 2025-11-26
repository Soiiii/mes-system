package com.mes.messystem.controller;

import com.mes.messystem.dto.*;
import com.mes.messystem.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class
WebSocketNotificationController {

    private final SimpMessagingTemplate messagingTemplate;
    private final DashboardService dashboardService;

    /**
     * Handle client request for dashboard data
     * Client sends to: /app/dashboard/request
     * Client receives at: /topic/dashboard
     */
    @MessageMapping("/dashboard/request")
    @SendTo("/topic/dashboard")
    public DashboardResponse handleDashboardRequest() {
        log.info("Dashboard data requested via WebSocket");
        return dashboardService.getDashboardData();
    }

    /**
     * Broadcast dashboard updates every 3 seconds
     * All clients subscribed to /topic/dashboard will receive updates
     */
    @Scheduled(fixedDelay = 3000)
    public void broadcastDashboardUpdate() {
        DashboardResponse data = dashboardService.getDashboardData();
        messagingTemplate.convertAndSend("/topic/dashboard", data);
        log.debug("Dashboard update broadcasted via WebSocket");
    }

    /**
     * Broadcast equipment status updates
     */
    public void broadcastEquipmentUpdate(EquipmentStatusSummary equipmentStatus) {
        messagingTemplate.convertAndSend("/topic/equipment", equipmentStatus);
        log.debug("Equipment update broadcasted: {}", equipmentStatus.getEquipmentName());
    }

    /**
     * Broadcast work order progress updates
     */
    public void broadcastWorkProgressUpdate(WorkProgressInfo workProgress) {
        messagingTemplate.convertAndSend("/topic/work-progress", workProgress);
        log.info("Work progress update broadcasted for WorkOrder: {}", workProgress.getWorkOrderId());
    }

    /**
     * Send alert/notification to all clients
     */
    public void broadcastAlert(String message, String severity) {
        AlertMessage alert = new AlertMessage(message, severity, System.currentTimeMillis());
        messagingTemplate.convertAndSend("/topic/alerts", alert);
        log.warn("Alert broadcasted: {} - {}", severity, message);
    }

    /**
     * Alert message DTO
     */
    public record AlertMessage(String message, String severity, long timestamp) {}
}
