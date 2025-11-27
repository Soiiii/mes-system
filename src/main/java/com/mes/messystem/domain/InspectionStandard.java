package com.mes.messystem.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InspectionStandard {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;            // 검사 항목 코드
    private String name;            // 검사 항목명
    private String category;        // 카테고리 (치수, 외관, 성능 등)
    
    private String standardValue;   // 기준값
    private String upperLimit;      // 상한값
    private String lowerLimit;      // 하한값
    private String unit;            // 단위

    @Enumerated(EnumType.STRING)
    private InspectionType applicableType;  // 적용 검사 유형

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private String description;
    private Boolean isActive;
}
