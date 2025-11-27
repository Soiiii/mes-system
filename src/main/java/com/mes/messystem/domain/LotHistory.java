package com.mes.messystem.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LotHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id")
    private Lot lot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id")
    private ProcessEntity process;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    private LocalDateTime processedAt;

    private Integer inputQuantity;   // 투입 수량
    private Integer outputQuantity;  // 산출 수량
    private Integer defectQuantity;  // 불량 수량

    @Enumerated(EnumType.STRING)
    private ProcessResult result;

    private String operator;  // 작업자
    private String remarks;   // 비고

    @PrePersist
    protected void onCreate() {
        processedAt = LocalDateTime.now();
    }
}
