package com.mes.messystem.controller;

import com.mes.messystem.domain.Product;
import com.mes.messystem.domain.WorkOrder;
import com.mes.messystem.dto.WorkOrderRequest;
import com.mes.messystem.repository.ProductRepository;
import com.mes.messystem.repository.WorkOrderRepository;
import com.mes.messystem.service.WorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/work-orders")
@CrossOrigin(originPatterns = "*")
public class WorkOrderController {

    private final WorkOrderService workOrderService;
    private final ProductRepository productRepository;
    private final WorkOrderRepository workOrderRepository;

    @GetMapping
    public List<WorkOrder> getAll() {
        return workOrderRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkOrder> getById(@PathVariable Long id) {
        return workOrderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public WorkOrder create(@RequestBody WorkOrderRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return workOrderService.createWorkOrder(product, request.getQuantity());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkOrder> update(@PathVariable Long id, @RequestBody WorkOrderRequest request) {
        return workOrderRepository.findById(id)
                .map(workOrder -> {
                    if (request.getProductId() != null) {
                        Product product = productRepository.findById(request.getProductId())
                                .orElseThrow(() -> new RuntimeException("Product not found"));
                        workOrder.setProduct(product);
                    }
                    if (request.getQuantity() != null) {
                        workOrder.setQuantity(request.getQuantity());
                    }
                    if (request.getPlannedStartDate() != null) {
                        workOrder.setPlannedStartDate(LocalDate.parse(request.getPlannedStartDate()));
                    }
                    if (request.getPlannedEndDate() != null) {
                        workOrder.setPlannedEndDate(LocalDate.parse(request.getPlannedEndDate()));
                    }
                    return ResponseEntity.ok(workOrderRepository.save(workOrder));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (workOrderRepository.existsById(id)) {
            workOrderRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
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