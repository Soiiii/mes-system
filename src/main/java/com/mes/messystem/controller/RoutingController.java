package com.mes.messystem.controller;

import com.mes.messystem.domain.ProcessRouting;
import com.mes.messystem.repository.ProcessRoutingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/routing")
@CrossOrigin(originPatterns = "*")
public class RoutingController {

    private final ProcessRoutingRepository routingRepository;

    @GetMapping("/product/{productId}")
    public List<ProcessRouting> getByProduct(@PathVariable Long productId) {
        return routingRepository.findByProductIdOrderBySequenceAsc(productId);
    }

    @GetMapping
    public List<ProcessRouting> getAll() {
        return routingRepository.findAll();
    }
}