package com.mes.messystem.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InspectionItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_id")
    private QualityInspection inspection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "standard_id")
    private InspectionStandard standard;

    private String measuredValue;    // 측정값
    private String standardValue;    // 기준값
    private String tolerance;        // 허용 오차

    @Enumerated(EnumType.STRING)
    private InspectionResult result; // 항목별 결과

    private String remarks;
}
