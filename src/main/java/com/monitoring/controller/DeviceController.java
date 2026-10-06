package com.monitoring.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
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
public ResponseEntity<?> addDevice(@RequestBody Device newDevice) {
    
    // 1. Ellenőrizzük, hogy a név üres-e
    if (newDevice.getName() == null || newDevice.getName().trim().isEmpty()) {
        return ResponseEntity.badRequest().body("Hiba: Az eszköz neve nem lehet üres!");
    }
    
    // IP-cím kimentése egy változóba az egyszerűbb olvashatóságért
    String ip = newDevice.getIpAddress();
    
    // 2. Ellenőrizzük, hogy az IP-cím üres-e
    if (ip == null || ip.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("Hiba: Az IP-cím nem lehet üres!");
    }

    // 3. IP-cím FORMÁTUM ellenőrzése (RegEx minta)
    String ipPattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    if (!ip.matches(ipPattern)) {
        return ResponseEntity.badRequest().body("Hiba: Érvénytelen IP-cím formátum! (Helyes pl.: 192.168.1.50)");
    }
    
if (monitoringService.existsByIpAddress(ip)) {
        return ResponseEntity.badRequest().body("Hiba: Ezzel az IP-címmel már létezik regisztrált eszköz!");
    }
    
    // Ha idáig eljutott, minden adat tökéletes, jöhet a mentés
    Device savedDevice = monitoringService.addDevice(newDevice);
    return ResponseEntity.ok(savedDevice);
}
    
    @PostMapping("/{id}/ping")
    public Device pingDevice(@PathVariable Long id) {
        return monitoringService.pingSingleDevice(id);
    }
}