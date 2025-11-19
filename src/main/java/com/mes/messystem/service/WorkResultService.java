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

@Service
@RequiredArgsConstructor
public class WorkResultService {

    private final WorkOrderRepository workOrderRepository;
    private final ProcessRepository processRepository;
    private final WorkResultRepository workResultRepository;

    public WorkResult completeProcess(Long workOrderId, Long processId, int goodQty, int badQty) {

        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow();

        ProcessEntity process = processRepository.findById(processId)
                .orElseThrow();

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
