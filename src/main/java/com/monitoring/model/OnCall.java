package com.monitoring.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class OnCall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String names;
    private String phones;
    
    // ÚJ MEZŐ: Részleg azonosítására
    private String department; 

    public OnCall() {}

    public OnCall(LocalDate date, String names, String phones, String department) {
        this.date = date;
        this.names = names;
        this.phones = phones;
        this.department = department;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getNames() { return names; }
    public void setNames(String names) { this.names = names; }
    public String getPhones() { return phones; }
    public void setPhones(String phones) { this.phones = phones; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}