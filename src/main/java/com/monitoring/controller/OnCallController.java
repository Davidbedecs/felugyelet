package com.monitoring.controller;

import java.time.LocalDate;
import java.util.List;

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

    @GetMapping("/{date}")
    public ResponseEntity<List<OnCall>> getOnCallByDate(@PathVariable String date) {
        LocalDate searchDate = LocalDate.parse(date);
        List<OnCall> onCalls = onCallRepository.findAllByDate(searchDate);
        
        if (onCalls.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(onCalls);
    }
}