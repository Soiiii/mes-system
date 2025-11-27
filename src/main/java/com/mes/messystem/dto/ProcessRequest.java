package com.mes.messystem.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessRequest {
    private String name;
    private String code;
    private String description;
    private Integer sequence;
}