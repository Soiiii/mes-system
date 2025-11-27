package com.mes.messystem.controller;

import com.mes.messystem.domain.Defect;
import com.mes.messystem.dto.DefectRequest;
import com.mes.messystem.repository.DefectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/defects")
public class DefectController {

    private final DefectRepository defectRepository;

    @GetMapping
    public List<Defect> getAll() {
        return defectRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Defect> getById(@PathVariable Long id) {
        return defectRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Defect create(@RequestBody DefectRequest request) {
        Defect defect = Defect.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return defectRepository.save(defect);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Defect> update(@PathVariable Long id, @RequestBody DefectRequest request) {
        return defectRepository.findById(id)
                .map(defect -> {
                    defect.setCode(request.getCode());
                    defect.setName(request.getName());
                    defect.setDescription(request.getDescription());
                    return ResponseEntity.ok(defectRepository.save(defect));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (defectRepository.existsById(id)) {
            defectRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/stats")
    public List<Map<String, Object>> getStats() {
        List<Defect> defects = defectRepository.findAll();
        return defects.stream()
                .map(defect -> {
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("defectCode", defect.getCode());
                    stat.put("defectName", defect.getName());
                    stat.put("count", (int) (Math.random() * 100));
                    return stat;
                })
                .collect(Collectors.toList());
    }
}