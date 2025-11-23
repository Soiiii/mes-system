package com.mes.messystem.repository;

import com.mes.messystem.domain.WorkResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkResultRepository extends JpaRepository<WorkResult, Long> {
    List<WorkResult> findByWorkOrderId(Long workOrderId);
}
