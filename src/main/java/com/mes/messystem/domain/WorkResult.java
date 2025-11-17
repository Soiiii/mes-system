package com.mes.messystem.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkResult {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private WorkOrder workOrder;

    @ManyToOne
    private ProcessEntity process;

    private int goodQty;

    private int badQty;

    private LocalDateTime timestamp;
}
