package com.mes.messystem.controller;

import com.mes.messystem.domain.ProcessEntity;
import com.mes.messystem.dto.ProcessRequest;
import com.mes.messystem.repository.ProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/processes")
public class ProcessController {

    private final ProcessRepository processRepository;

    @GetMapping
    public List<ProcessEntity> getAll() {
        return processRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcessEntity> getById(@PathVariable Long id) {
        return processRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ProcessEntity create(@RequestBody ProcessRequest request) {
        ProcessEntity process = ProcessEntity.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .sequence(request.getSequence() != null ? request.getSequence() : 0)
                .build();
        return processRepository.save(process);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProcessEntity> update(@PathVariable Long id, @RequestBody ProcessRequest request) {
        return processRepository.findById(id)
                .map(process -> {
                    process.setName(request.getName());
                    process.setCode(request.getCode());
                    process.setDescription(request.getDescription());
                    if (request.getSequence() != null) {
                        process.setSequence(request.getSequence());
                    }
                    return ResponseEntity.ok(processRepository.save(process));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (processRepository.existsById(id)) {
            processRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}