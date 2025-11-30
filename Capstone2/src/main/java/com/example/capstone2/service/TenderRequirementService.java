package com.example.capstone2.service;


import com.example.capstone2.model.TenderRequirement;
import com.example.capstone2.repository.TenderRequirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TenderRequirementService {

    private final TenderRequirementRepository tenderRequirementRepository;

    public List<TenderRequirement> getAllTenderRequirements() {
        return tenderRequirementRepository.findAll();
    }

    public TenderRequirement getTenderRequirementById(Integer id) {
        return tenderRequirementRepository.findTenderRequirementById(id);
    }

    public List<TenderRequirement> getTenderRequirementsByTenderId(Integer tenderId) {
        return tenderRequirementRepository.findTenderRequirementsByTenderId(tenderId);
    }

    public boolean addTenderRequirement(TenderRequirement tenderRequirement) {
        tenderRequirementRepository.save(tenderRequirement);
        return true;
    }

    public boolean updateTenderRequirement(Integer id, TenderRequirement tenderRequirement) {
        TenderRequirement oldRequirement = tenderRequirementRepository.findTenderRequirementById(id);
        if (oldRequirement == null) {
            return false;
        }

        oldRequirement.setTenderId(tenderRequirement.getTenderId());
        oldRequirement.setRequirementText(tenderRequirement.getRequirementText());
        oldRequirement.setPriority(tenderRequirement.getPriority());

        tenderRequirementRepository.save(oldRequirement);
        return true;
    }

    public boolean deleteTenderRequirement(Integer id) {
        TenderRequirement requirement = tenderRequirementRepository.findTenderRequirementById(id);
        if (requirement == null) {
            return false;
        }
        tenderRequirementRepository.delete(requirement);
        return true;
    }
}
