package com.mes.messystem.service;

import com.mes.messystem.domain.*;
import com.mes.messystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkOrderService {
    private final WorkOrderRepository workOrderRepository;

    public WorkOrder createWorkOrder(Product product, int quantity) {
        WorkOrder wo = WorkOrder.builder()
                .product(product)
                .quantity(quantity)
                .status(WorkOrderStatus.PLANNED)
                .build();

        return workOrderRepository.save(wo);
    }

    public WorkOrder start(Long id) {
        WorkOrder wo = workOrderRepository.findById(id)
                .orElseThrow();

        wo.setStatus(WorkOrderStatus.IN_PROGRESS);
        return workOrderRepository.save(wo);
    }

    public WorkOrder finish(Long id) {
        WorkOrder wo = workOrderRepository.findById(id)
                .orElseThrow();

        wo.setStatus(WorkOrderStatus.COMPLETED);
        return workOrderRepository.save(wo);
    }
}
