package com.mes.messystem.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Product product;

    private int quantity;

    @Enumerated(EnumType.STRING)
    private WorkOrderStatus status;

    private LocalDateTime startTime;
    private LocalDateTime finishTime;

    private LocalDate plannedStartDate;
    private LocalDate plannedEndDate;
}
