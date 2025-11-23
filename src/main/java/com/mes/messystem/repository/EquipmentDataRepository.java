package com.mes.messystem.repository;

import com.mes.messystem.domain.EquipmentData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentDataRepository extends JpaRepository<EquipmentData, Long> {
    List<EquipmentData> findByEquipmentIdOrderByTimestampDesc(Long equipmentId);
}
