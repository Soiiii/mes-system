package com.mes.messystem.repository;

import com.mes.messystem.domain.Lot;
import com.mes.messystem.domain.LotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LotRepository extends JpaRepository<Lot, Long> {
    Optional<Lot> findByLotNumber(String lotNumber);
    
    List<Lot> findByProductId(Long productId);
    
    List<Lot> findByWorkOrderId(Long workOrderId);
    
    List<Lot> findByStatus(LotStatus status);
    
    List<Lot> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT l FROM Lot l WHERE l.lotNumber LIKE %:keyword% OR l.product.name LIKE %:keyword%")
    List<Lot> searchByKeyword(String keyword);
    
    @Query("SELECT COUNT(l) FROM Lot l WHERE l.createdAt >= :date")
    Long countByCreatedAtAfter(LocalDateTime date);
}
