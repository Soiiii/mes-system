package com.mes.messystem.service;

import com.mes.messystem.domain.ProcessEntity;
import com.mes.messystem.domain.WorkOrder;
import com.mes.messystem.domain.WorkResult;
import com.mes.messystem.repository.ProcessRepository;
import com.mes.messystem.repository.WorkOrderRepository;
import com.mes.messystem.repository.WorkResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkResultService {

    private final WorkOrderRepository workOrderRepository;
    private final ProcessRepository processRepository;
    private final WorkResultRepository workResultRepository;

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

        // Save WorkResult
        WorkResult result = WorkResult.builder()
                .workOrder(workOrder)
                .process(process)
                .goodQty(goodQty)
                .badQty(badQty)
                .workTime(LocalDateTime.now())
                .build();

        return workResultRepository.save(result);
    }

}
