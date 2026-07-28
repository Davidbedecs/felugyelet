package com.monitoring.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monitoring.model.Device;
import com.monitoring.service.MonitoringService;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final MonitoringService monitoringService;

    public DeviceController(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    
    @GetMapping
    public List<Device> getDevices() {
        return monitoringService.getAllDevices();
    }


    @PostMapping
    public Device addDevice(@RequestBody Device newDevice) {
        return monitoringService.addDevice(newDevice);
    }
    @PostMapping("/{id}/ping")
    public Device pingDevice(@PathVariable Long id) {
        return monitoringService.pingSingleDevice(id);
    }
}