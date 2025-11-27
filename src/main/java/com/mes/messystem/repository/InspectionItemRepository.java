package com.mes.messystem.repository;

import com.mes.messystem.domain.InspectionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectionItemRepository extends JpaRepository<InspectionItem, Long> {
    List<InspectionItem> findByInspectionId(Long inspectionId);
}
