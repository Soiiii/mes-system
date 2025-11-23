package com.mes.messystem.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductCreateRequest {
    private String name;
    private List<Long> processIds;
}
