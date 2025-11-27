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
public class QualityInspection {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String inspectionNumber;  // INS-YYYYMMDD-XXXX

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id")
    private Lot lot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id")
    private ProcessEntity process;

    @Enumerated(EnumType.STRING)
    private InspectionType type;  // INCOMING, IN_PROCESS, FINAL, OUTGOING

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private InspectionStatus status = InspectionStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private InspectionResult result;  // PASS, FAIL, CONDITIONAL_PASS

    private Integer sampleSize;      // 샘플 수량
    private Integer passedCount;     // 합격 수
    private Integer failedCount;     // 불합격 수

    private String inspector;        // 검사자
    private LocalDateTime inspectionDate;
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "inspection", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InspectionItem> items = new ArrayList<>();

    private String remarks;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
