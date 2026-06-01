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

    private LocalDate date; // A pontos dátum (pl. 2026-05-31)
    
    private String names; // A készenlétesek nevei (pl. "Gombos Péter, Varga Zsolt, Komlósi István")
    private String phones; // A hozzájuk tartozó telefonszámok

    public OnCall() {}

    public OnCall(LocalDate date, String names, String phones) {
        this.date = date;
        this.names = names;
        this.phones = phones;
    }

    // Getterek és Setterek
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getNames() { return names; }
    public void setNames(String names) { this.names = names; }
    public String getPhones() { return phones; }
    public void setPhones(String phones) { this.phones = phones; }
}