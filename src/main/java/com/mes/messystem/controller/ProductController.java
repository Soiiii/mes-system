package com.mes.messystem.controller;

import com.mes.messystem.domain.ProcessEntity;
import com.mes.messystem.domain.Product;
import com.mes.messystem.dto.ProductCreateRequest;
import com.mes.messystem.repository.ProcessRepository;
import com.mes.messystem.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductRepository productRepository;
    private final ProcessRepository processRepository;

    @PostMapping
    public Product create(@RequestBody ProductCreateRequest req) {
        List<ProcessEntity> processes = processRepository.findAllById(req.getProcessIds());

        Product product = Product.builder()
                .name(req.getName())
                .processes(processes)
                .build();

        return productRepository.save(product);
    }

    @GetMapping
    public List<Product> all() {
        return productRepository.findAll();
    }
}
