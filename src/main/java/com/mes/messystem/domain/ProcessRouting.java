package com.mes.messystem.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessRouting {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "process_id")
    private ProcessEntity process;

    @ManyToOne
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    private Integer sequence;
    private Integer standardTime; // in minutes
}