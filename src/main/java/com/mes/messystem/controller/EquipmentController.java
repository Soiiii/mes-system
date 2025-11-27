package com.mes.messystem.controller;

import com.mes.messystem.domain.Equipment;
import com.mes.messystem.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/equipment")
@CrossOrigin(originPatterns = "*")
public class EquipmentController {

    private final EquipmentRepository equipmentRepository;
    private final Map<Long, SseEmitter> equipmentEmitters = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @GetMapping
    public List<Equipment> getAll() {
        return equipmentRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipment> getById(@PathVariable Long id) {
        return equipmentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status")
    public List<Equipment> getStatus() {
        return equipmentRepository.findAll();
    }

    /**
     * SSE endpoint for real-time equipment data
     */
    @GetMapping(value = "/{id}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamEquipmentData(@PathVariable Long id) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        equipmentEmitters.put(id, emitter);

        emitter.onCompletion(() -> equipmentEmitters.remove(id));
        emitter.onTimeout(() -> equipmentEmitters.remove(id));
        emitter.onError(e -> equipmentEmitters.remove(id));

        // Send initial mock data
        try {
            emitter.send(SseEmitter.event()
                    .name("equipment-data")
                    .data(generateMockEquipmentData()));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        // Schedule periodic updates every 2 seconds
        scheduler.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("equipment-data")
                        .data(generateMockEquipmentData()));
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }, 2000, 2000, TimeUnit.MILLISECONDS);

        return emitter;
    }

    private Map<String, Object> generateMockEquipmentData() {
        return Map.of(
                "temperature", 20 + Math.random() * 10,
                "vibration", 0.5 + Math.random() * 2,
                "pressure", 1.0 + Math.random() * 0.5
        );
    }
}