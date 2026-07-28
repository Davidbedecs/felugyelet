package com.monitoring.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PowerSupply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location; 
    private String exactLocation; 
    private LocalDate measurementDate; 
    private String measuredBy; 
    private String chargerType; 
    private String batteryGroup; 
    private String groupVoltage; 
    private String batteryType; 
    private String capacity; 
    private String installedDate; 
    private String temperature; 
    private String instrument; 

    public PowerSupply() {}

    // Getterek és Setterek
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getExactLocation() { return exactLocation; }
    public void setExactLocation(String exactLocation) { this.exactLocation = exactLocation; }
    public LocalDate getMeasurementDate() { return measurementDate; }
    public void setMeasurementDate(LocalDate measurementDate) { this.measurementDate = measurementDate; }
    public String getMeasuredBy() { return measuredBy; }
    public void setMeasuredBy(String measuredBy) { this.measuredBy = measuredBy; }
    public String getChargerType() { return chargerType; }
    public void setChargerType(String chargerType) { this.chargerType = chargerType; }
    public String getBatteryGroup() { return batteryGroup; }
    public void setBatteryGroup(String batteryGroup) { this.batteryGroup = batteryGroup; }
    public String getGroupVoltage() { return groupVoltage; }
    public void setGroupVoltage(String groupVoltage) { this.groupVoltage = groupVoltage; }
    public String getBatteryType() { return batteryType; }
    public void setBatteryType(String batteryType) { this.batteryType = batteryType; }
    public String getCapacity() { return capacity; }
    public void setCapacity(String capacity) { this.capacity = capacity; }
    public String getInstalledDate() { return installedDate; }
    public void setInstalledDate(String installedDate) { this.installedDate = installedDate; }
    public String getTemperature() { return temperature; }
    public void setTemperature(String temperature) { this.temperature = temperature; }
    public String getInstrument() { return instrument; }
    public void setInstrument(String instrument) { this.instrument = instrument; }
}