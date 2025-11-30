package com.example.capstone2.repository;

import com.example.capstone2.model.TenderRequirement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TenderRequirementRepository extends JpaRepository<TenderRequirement, Integer> {

    TenderRequirement findTenderRequirementById(Integer id);

    List<TenderRequirement> findTenderRequirementsByTenderId(Integer tenderId);
}
