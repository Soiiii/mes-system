package com.mes.messystem.repository;

import com.mes.messystem.domain.ProcessRouting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessRoutingRepository extends JpaRepository<ProcessRouting, Long> {
    List<ProcessRouting> findByProductIdOrderBySequenceAsc(Long productId);
}