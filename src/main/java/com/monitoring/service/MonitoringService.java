package com.monitoring.service;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.monitoring.model.Device;
import com.monitoring.repository.DeviceRepository;

@Service
public class MonitoringService {

    private final DeviceRepository deviceRepository;

   
    public MonitoringService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    @Scheduled(fixedRate = 30000)
    public void checkDevices() {
        
        List<Device> devices = deviceRepository.findAll();
        
        for (Device device : devices) {
            try {
                InetAddress address = InetAddress.getByName(device.getIpAddress());
                boolean reachable = address.isReachable(3000); 
                device.setAlive(reachable);
                device.setLastChecked(LocalDateTime.now());
            } catch (Exception e) {
                device.setAlive(false);
                device.setLastChecked(LocalDateTime.now());
            }
            
        
            deviceRepository.save(device);
        }
    }

    // Új eszköz mentése az adatbázisba
    public Device addDevice(Device device) {
        device.setAlive(false); // Alapértelmezetten offline-ként indul, amíg az időzítő le nem pingeli
        return deviceRepository.save(device);
    }

    
    public Device pingSingleDevice(Long id) {
        
        Device device = deviceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Eszköz nem található az adatbázisban: " + id));

        try {
            InetAddress address = InetAddress.getByName(device.getIpAddress());
            boolean reachable = address.isReachable(3000); 
            device.setAlive(reachable);
            device.setLastChecked(LocalDateTime.now());
        } catch (Exception e) {
            device.setAlive(false);
            device.setLastChecked(LocalDateTime.now());
        }
        
        return deviceRepository.save(device);
    }
    public boolean existsByIpAddress(String ipAddress) {
    return deviceRepository.existsByIpAddress(ipAddress);
}
}