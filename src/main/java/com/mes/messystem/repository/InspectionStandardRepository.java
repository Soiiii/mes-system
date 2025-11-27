package com.mes.messystem.repository;

import com.mes.messystem.domain.InspectionStandard;
import com.mes.messystem.domain.InspectionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectionStandardRepository extends JpaRepository<InspectionStandard, Long> {
    List<InspectionStandard> findByProductId(Long productId);
    
    List<InspectionStandard> findByApplicableType(InspectionType type);
    
    List<InspectionStandard> findByProductIdAndApplicableType(Long productId, InspectionType type);
    
    List<InspectionStandard> findByIsActive(Boolean isActive);
}
