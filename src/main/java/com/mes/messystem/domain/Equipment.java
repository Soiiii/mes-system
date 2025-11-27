package com.mes.messystem.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String location;

    @Enumerated(EnumType.STRING)
    private EquipmentType type;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EquipmentStatus status = EquipmentStatus.IDLE;

    private Integer sequence;
}
