package com.mes.messystem.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String lotNumber;  // LOT-YYYYMMDD-XXXX 형식

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id")
    private WorkOrder workOrder;

    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private LotStatus status = LotStatus.CREATED;

    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "lot", cascade = CascadeType.ALL)
    @Builder.Default
    private List<LotHistory> histories = new ArrayList<>();

    private String remarks;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
