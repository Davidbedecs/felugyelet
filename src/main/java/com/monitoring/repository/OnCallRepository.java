package com.monitoring.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monitoring.model.OnCall;

@Repository
public interface OnCallRepository extends JpaRepository<OnCall, Long> {
    
    Optional<OnCall> findByDateAndDepartment(LocalDate date, String department);
    
   
    List<OnCall> findAllByDate(LocalDate date);
}