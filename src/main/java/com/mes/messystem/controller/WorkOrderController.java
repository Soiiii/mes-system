package com.mes.messystem.controller;

import com.mes.messystem.domain.Product;
import com.mes.messystem.domain.WorkOrder;
import com.mes.messystem.repository.ProductRepository;
import com.mes.messystem.service.WorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/work-order")
public class WorkOrderController {

    private final WorkOrderService workOrderService;
    private final ProductRepository productRepository;

    @PostMapping
    public WorkOrder create(@RequestParam Long productId,
                            @RequestParam int quantity) {

        Product product = productRepository.findById(productId)
                .orElseThrow();

        return workOrderService.createWorkOrder(product, quantity);
    }

    @PostMapping("/{id}/start")
    public WorkOrder start(@PathVariable Long id) {
        return workOrderService.start(id);
    }

    @PostMapping("/{id}/finish")
    public WorkOrder finish(@PathVariable Long id) {
        return workOrderService.finish(id);
    }
}
