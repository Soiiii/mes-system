package com.mes.messystem.service;

import com.mes.messystem.domain.*;
import com.mes.messystem.repository.ProcessRepository;
import com.mes.messystem.repository.ProductRepository;
import com.mes.messystem.repository.WorkOrderRepository;
import com.mes.messystem.repository.WorkResultRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class WorkResultServiceTest {
    @Autowired
    private WorkResultService workResultService;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private WorkResultRepository workResultRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    private Long workOrderId;
    private Long cuttingId;
    private Long assemblyId;

    @BeforeEach
    void setup() {
        // clear db data
        workResultRepository.deleteAll();
        workOrderRepository.deleteAll();

        // Clear many-to-many relationships before deleting
        productRepository.findAll().forEach(product -> {
            product.getProcesses().clear();
            productRepository.save(product);
        });
        entityManager.flush();
        productRepository.deleteAll();
        processRepository.deleteAll();

        // create process
        ProcessEntity cutting = ProcessEntity.builder()
                .name("Cutting")
                .sequence(1)
                .build();

        ProcessEntity assembly = ProcessEntity.builder()
                .name("Assembly")
                .sequence(2)
                .build();

        processRepository.save(cutting);
        processRepository.save(assembly);

        // create product
        Product product = Product.builder()
                .name("TestProduct")
                .build();

        // product <-> processes
        product.addProcess(cutting);
        product.addProcess(assembly);

        // save product
        productRepository.save(product);

        // create workorder
        WorkOrder wo = new WorkOrder();
        wo.setProduct(product);
        workOrderRepository.save(wo);

        workOrderId = wo.getId();
        cuttingId = cutting.getId();
        assemblyId = assembly.getId();
    }

    @Test
    void testProcessCompletedInCorrectOrder() {
        // when
        WorkResult result1 = workResultService.completeProcess(workOrderId, cuttingId, 10, 0);

        // then
        assertNotNull(result1);
        assertEquals(cuttingId, result1.getProcess().getId());

        // Assembly (Sequence 2)
        WorkResult result2 = workResultService.completeProcess(workOrderId, assemblyId, 8, 2);

        assertNotNull(result2);
        assertEquals(assemblyId, result2.getProcess().getId());
    }


    @Test
    void testProcessOrderValidationFail() {
        // Assembly(2) -> Cutting(1)
        assertThrows(
                IllegalArgumentException.class,
                () -> workResultService.completeProcess(workOrderId, assemblyId, 10, 0)
        );
    }

    @Test
    void testWorkOrderStatusUpdates() {
        // Given: Check the initial WorkOrder status
        WorkOrder workOrder = workOrderRepository.findById(workOrderId).orElseThrow();
        assertNull(workOrder.getStatus()); // 초기 상태는 null

        // When: First process is completed (Cutting)
        workResultService.completeProcess(workOrderId, cuttingId, 10, 0);

        // Then: Status changes to STARTED
        workOrder = workOrderRepository.findById(workOrderId).orElseThrow();
        assertEquals(WorkOrderStatus.STARTED, workOrder.getStatus());

        // When: Last process is completed (Assembly)
        workResultService.completeProcess(workOrderId, assemblyId, 8, 2);

        // Then: Status changes to COMPLETED and finishTime is set
        workOrder = workOrderRepository.findById(workOrderId).orElseThrow();
        assertEquals(WorkOrderStatus.COMPLETED, workOrder.getStatus());
        assertNotNull(workOrder.getFinishTime());
    }

    @Test
    void testWorkOrderStatusWithThreeProcesses() {
        // Given: Create a product with 3 processes
        workResultRepository.deleteAll();
        workOrderRepository.deleteAll();
        productRepository.findAll().forEach(product -> {
            product.getProcesses().clear();
            productRepository.save(product);
        });
        entityManager.flush();
        productRepository.deleteAll();
        processRepository.deleteAll();

        ProcessEntity cutting = ProcessEntity.builder()
                .name("Cutting")
                .sequence(1)
                .build();

        ProcessEntity assembly = ProcessEntity.builder()
                .name("Assembly")
                .sequence(2)
                .build();

        ProcessEntity packaging = ProcessEntity.builder()
                .name("Packaging")
                .sequence(3)
                .build();

        processRepository.save(cutting);
        processRepository.save(assembly);
        processRepository.save(packaging);

        Product product = Product.builder()
                .name("TestProduct")
                .build();

        product.addProcess(cutting);
        product.addProcess(assembly);
        product.addProcess(packaging);
        productRepository.save(product);

        WorkOrder wo = new WorkOrder();
        wo.setProduct(product);
        workOrderRepository.save(wo);

        Long woId = wo.getId();
        Long cuttingId = cutting.getId();
        Long assemblyId = assembly.getId();
        Long packagingId = packaging.getId();

        // When & Then: First process done → STARTED
        workResultService.completeProcess(woId, cuttingId, 10, 0);
        wo = workOrderRepository.findById(woId).orElseThrow();
        assertEquals(WorkOrderStatus.STARTED, wo.getStatus());

        // When & Then: Second process done → IN_PROGRESS
        workResultService.completeProcess(woId, assemblyId, 10, 0);
        wo = workOrderRepository.findById(woId).orElseThrow();
        assertEquals(WorkOrderStatus.IN_PROGRESS, wo.getStatus());

        // When & Then: Last process done → COMPLETED
        workResultService.completeProcess(woId, packagingId, 10, 0);
        wo = workOrderRepository.findById(woId).orElseThrow();
        assertEquals(WorkOrderStatus.COMPLETED, wo.getStatus());
        assertNotNull(wo.getFinishTime());
    }

    @Test
    void testWorkOrderRejectedByHighDefectRate() {
        // When: Trying to complete a process with a defect rate of 30% or higher
        // Example: 4 defects out of 10 units = 40%
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> workResultService.completeProcess(workOrderId, cuttingId, 6, 4)
        );

        // Then: Status changes to REJECTED and an exception is thrown
        assertTrue(exception.getMessage().contains("Defect rate too high"));
        WorkOrder workOrder = workOrderRepository.findById(workOrderId).orElseThrow();
        assertEquals(WorkOrderStatus.REJECTED, workOrder.getStatus());
    }

    @Test
    void testWorkOrderAcceptedByLowDefectRate() {
        // When: Defect rate is below 30% (2 defects out of 10 = 20%)
        WorkResult result = workResultService.completeProcess(workOrderId, cuttingId, 8, 2);

        // Then: Process completes normally and status becomes STARTED
        assertNotNull(result);
        WorkOrder workOrder = workOrderRepository.findById(workOrderId).orElseThrow();
        assertEquals(WorkOrderStatus.STARTED, workOrder.getStatus());
    }

}
