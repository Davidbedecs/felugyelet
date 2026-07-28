package com.monitoring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monitoring.model.Handover;

@Repository
public interface HandoverRepository extends JpaRepository<Handover, Long> {
   
    List<Handover> findAllByOrderByTimestampDesc();
}