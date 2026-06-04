package com.monitoring.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monitoring.model.Handover;
import com.monitoring.repository.HandoverRepository;

@RestController
@RequestMapping("/api/handover")
public class HandoverController {

    private final HandoverRepository handoverRepository;

    public HandoverController(HandoverRepository handoverRepository) {
        this.handoverRepository = handoverRepository;
    }

    @GetMapping
    public List<Handover> getAllHandovers() {
        return handoverRepository.findAllByOrderByTimestampDesc();
    }

    @PostMapping
    public Handover addHandover(@RequestBody Handover handover) {
        handover.setTimestamp(LocalDateTime.now()); // Mentéskor automatikusan ráütjük a pontos időt
        return handoverRepository.save(handover);
    }
}