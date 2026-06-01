package com.monitoring.repository;

import com.monitoring.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    // Készen is vagyunk! 
    // Nem kell ide írni semmit, a findAll(), save(), delete() stb. metódusokat automatikusan megkapod.
}