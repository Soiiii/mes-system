package com.mes.messystem.service;

import com.mes.messystem.domain.ProcessEntity;
import com.mes.messystem.domain.Product;
import com.mes.messystem.domain.WorkOrder;
import com.mes.messystem.domain.WorkResult;
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

}
