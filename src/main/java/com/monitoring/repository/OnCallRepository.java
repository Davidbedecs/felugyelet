package com.monitoring.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monitoring.model.OnCall;

@Repository
public interface OnCallRepository extends JpaRepository<OnCall, Long> {
    // Automatikusan keres dátum alapján az adatbázisban
    Optional<OnCall> findByDate(LocalDate date);
}