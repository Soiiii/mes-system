package com.mes.messystem.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefectRequest {
    private String code;
    private String name;
    private String description;
}
