package com.monitoring.controller;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monitoring.model.OnCall;
import com.monitoring.repository.OnCallRepository;

@RestController
@RequestMapping("/api/oncall")
public class OnCallController {

    private final OnCallRepository onCallRepository;

    public OnCallController(OnCallRepository onCallRepository) {
        this.onCallRepository = onCallRepository;
    }

    // Keresés pontos dátum alapján (pl. /api/oncall/2026-05-31)
    @GetMapping("/{date}")
    public ResponseEntity<OnCall> getOnCallByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        Optional<OnCall> onCall = onCallRepository.findByDate(date);
        
        // Ha van találat, visszaadjuk, ha nincs, 404 Not Found
        return onCall.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }
}