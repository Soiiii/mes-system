package com.mes.messystem.service;

import com.mes.messystem.controller.WebSocketNotificationController;
import com.mes.messystem.domain.ProcessEntity;
import com.mes.messystem.domain.WorkOrder;
import com.mes.messystem.domain.WorkOrderStatus;
import com.mes.messystem.domain.WorkResult;
import com.mes.messystem.dto.WorkProgressInfo;
import com.mes.messystem.repository.ProcessRepository;
import com.mes.messystem.repository.WorkOrderRepository;
import com.mes.messystem.repository.WorkResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkResultService {

    private final WorkOrderRepository workOrderRepository;
    private final ProcessRepository processRepository;
    private final WorkResultRepository workResultRepository;
    private final WebSocketNotificationController webSocketNotificationController;

    // Defect rate threshold (over than 30% REJECTED)
    private static final double DEFECT_RATE_THRESHOLD = 0.30;

    @Transactional
    public WorkResult completeProcess(Long workOrderId, Long processId, int goodQty, int badQty) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new RuntimeException("WorkOrder not found"));

        ProcessEntity process = processRepository.findById(processId)
                .orElseThrow(() -> new RuntimeException("Process not found"));

        // Get full process sequence for the product
        List<ProcessEntity> processList = workOrder.getProduct().getProcesses()
                .stream()
                .sorted(Comparator.comparingInt(ProcessEntity::getSequence))
                .toList();

        // Load all completed results for this WorkOrder
        List<WorkResult> completed = workResultRepository.findByWorkOrderId(workOrderId);

        int completedCount = completed.size();

        // Determine the next process that must be completed
        ProcessEntity expectedProcess = processList.get(completedCount);

        // Validate sequence
        if (!expectedProcess.getId().equals(processId)) {
            throw new IllegalArgumentException(
                    "Current is " + expectedProcess.getName() + "."
            );
        }

        // Check defect rate
        double defectRate = calculateDefectRate(goodQty, badQty);
        if (defectRate > DEFECT_RATE_THRESHOLD) {
            workOrder.setStatus(WorkOrderStatus.REJECTED);
            workOrderRepository.save(workOrder);
            log.warn("WorkOrder {} REJECTED - Defect rate: {:.2f}%", workOrderId, defectRate * 100);
            throw new IllegalArgumentException(
                    String.format("Defect rate too high: %.2f%% (threshold: %.2f%%)",
                            defectRate * 100, DEFECT_RATE_THRESHOLD * 100)
            );
        }

        // Save WorkResult
        WorkResult result = WorkResult.builder()
                .workOrder(workOrder)
                .process(process)
                .goodQty(goodQty)
                .badQty(badQty)
                .workTime(LocalDateTime.now())
                .build();

        workResultRepository.save(result);

        // Update WorkOrder status based on progress
        updateWorkOrderStatus(workOrder, completedCount + 1, processList.size());

        return result;
    }

    /**
     * Calculate defect rate
     */
    private double calculateDefectRate(int goodQty, int badQty) {
        int totalQty = goodQty + badQty;
        if (totalQty == 0) {
            return 0.0;
        }
        return (double) badQty / totalQty;
    }

    /**
     * Update WorkOrder status
     * - STARTED -> IN_PROGRESS -> COMPLETED
     */
    private void updateWorkOrderStatus(WorkOrder workOrder, int completedCount, int totalProcessCount) {
        WorkOrderStatus newStatus;

        if (completedCount == 1) {
            newStatus = WorkOrderStatus.STARTED;
        } else if (completedCount < totalProcessCount) {
            newStatus = WorkOrderStatus.IN_PROGRESS;
        } else {
            newStatus = WorkOrderStatus.COMPLETED;
            workOrder.setFinishTime(LocalDateTime.now());
        }

        workOrder.setStatus(newStatus);
        workOrderRepository.save(workOrder);

        log.info("WorkOrder {} status updated: {} ({}/{})",
                workOrder.getId(), newStatus, completedCount, totalProcessCount);

        // Broadcast work progress update via WebSocket
        double progressPercentage = totalProcessCount > 0
                ? (double) completedCount / totalProcessCount * 100
                : 0.0;

        WorkProgressInfo progressInfo = WorkProgressInfo.builder()
                .workOrderId(workOrder.getId())
                .productName(workOrder.getProduct().getName())
                .status(newStatus)
                .totalProcesses(totalProcessCount)
                .completedProcesses(completedCount)
                .progressPercentage(Math.round(progressPercentage * 100.0) / 100.0)
                .build();

        webSocketNotificationController.broadcastWorkProgressUpdate(progressInfo);

        // Send alert if REJECTED
        if (newStatus == WorkOrderStatus.REJECTED) {
            webSocketNotificationController.broadcastAlert(
                    "WorkOrder #" + workOrder.getId() + " rejected due to high defect rate",
                    "ERROR"
            );
        }
    }

}
