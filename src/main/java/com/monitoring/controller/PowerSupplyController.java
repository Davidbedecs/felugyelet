package com.monitoring.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monitoring.model.PowerSupply;
import com.monitoring.repository.PowerSupplyRepository;

@RestController
@RequestMapping("/api/powersupply")
public class PowerSupplyController {

    private final PowerSupplyRepository repository;

    public PowerSupplyController(PowerSupplyRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<PowerSupply> getAllPowerSupplies() {
        return repository.findAll();
    }
}