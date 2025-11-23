package com.mes.messystem.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentData {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Equipment equipment;

    @Enumerated(EnumType.STRING)
    private EquipmentStatus status;

    private Double temperature;

    private Integer productionSpeed;

    private LocalDateTime timestamp;
}
