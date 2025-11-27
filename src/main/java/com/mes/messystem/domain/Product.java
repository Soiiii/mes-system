package com.mes.messystem.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String code;
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "product_process",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "process_id")
    )
    @Builder.Default
    private List<ProcessEntity> processes = new ArrayList<>();

    public void addProcess(ProcessEntity process) {
        if (!processes.contains(process)) {
            processes.add(process);
        }
    }

}
