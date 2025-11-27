package com.mes.messystem.repository;

import com.mes.messystem.domain.InspectionResult;
import com.mes.messystem.domain.InspectionStatus;
import com.mes.messystem.domain.InspectionType;
import com.mes.messystem.domain.QualityInspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QualityInspectionRepository extends JpaRepository<QualityInspection, Long> {
    Optional<QualityInspection> findByInspectionNumber(String inspectionNumber);
    
    List<QualityInspection> findByLotId(Long lotId);
    
    List<QualityInspection> findByStatus(InspectionStatus status);
    
    List<QualityInspection> findByType(InspectionType type);
    
    List<QualityInspection> findByResult(InspectionResult result);
    
    List<QualityInspection> findByInspectionDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(qi) FROM QualityInspection qi WHERE qi.createdAt >= :date")
    Long countByCreatedAtAfter(LocalDateTime date);
    
    @Query("SELECT qi FROM QualityInspection qi WHERE qi.lot.lotNumber LIKE %:keyword% OR qi.inspectionNumber LIKE %:keyword%")
    List<QualityInspection> searchByKeyword(String keyword);
}
