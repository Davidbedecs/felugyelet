package com.monitoring.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monitoring.model.PowerSupply;

@Repository
public interface PowerSupplyRepository extends JpaRepository<PowerSupply, Long> {
    // Megkeresi, hogy az adott helyszínen, az adott napon van-e már rögzítve mérés
    Optional<PowerSupply> findByLocationAndExactLocationAndMeasurementDate(
            String location, String exactLocation, LocalDate measurementDate);
}