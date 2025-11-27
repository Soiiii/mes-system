package com.mes.messystem.repository;

import com.mes.messystem.domain.Defect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefectRepository extends JpaRepository<Defect, Long> {
}