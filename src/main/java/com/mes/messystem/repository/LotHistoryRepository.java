package com.mes.messystem.repository;

import com.mes.messystem.domain.LotHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LotHistoryRepository extends JpaRepository<LotHistory, Long> {
    List<LotHistory> findByLotId(Long lotId);
    
    List<LotHistory> findByLotIdOrderByProcessedAtAsc(Long lotId);
    
    List<LotHistory> findByProcessId(Long processId);
    
    List<LotHistory> findByEquipmentId(Long equipmentId);
    
    @Query("SELECT lh FROM LotHistory lh WHERE lh.lot.lotNumber = :lotNumber ORDER BY lh.processedAt ASC")
    List<LotHistory> findByLotNumberOrderByProcessedAt(String lotNumber);
}
