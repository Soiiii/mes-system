package com.mes.messystem.controller;

import com.mes.messystem.domain.WorkResult;
import com.mes.messystem.dto.WorkResultRequest;
import com.mes.messystem.service.WorkResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/work-order")
public class WorkResultController {

    private final WorkResultService workResultService;

    @PostMapping("/{id}/process/{processId}/complete")
    public WorkResult complete(
            @PathVariable Long id,
            @PathVariable Long processId,
            @RequestBody WorkResultRequest req) {

        return workResultService.completeProcess(id, processId, req.getGoodQty(), req.getBadQty());
    }
}
