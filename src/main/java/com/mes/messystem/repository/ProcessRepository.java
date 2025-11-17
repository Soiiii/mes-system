package com.mes.messystem.repository;

import com.mes.messystem.domain.ProcessEntity;
import com.mes.messystem.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessRepository extends JpaRepository<ProcessEntity, Long> {
}
