package com.monitoring.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Device {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Az ID immár szám alapú (Long), és az adatbázis generálja automatikusan

    private String name;
    private String ipAddress;
    private boolean isAlive;
    private LocalDateTime lastChecked;

    // A JPA (Hibernate) miatt KÖTELEZŐ egy paraméter nélküli üres konstruktor
    public Device() {
    }

    public Device(String name, String ipAddress) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.isAlive = false;
    }

    // Getterek és Setterek
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public boolean isAlive() { return isAlive; }
    public void setAlive(boolean alive) { this.isAlive = alive; }
    
    public LocalDateTime getLastChecked() { return lastChecked; }
    public void setLastChecked(LocalDateTime lastChecked) { this.lastChecked = lastChecked; }
}