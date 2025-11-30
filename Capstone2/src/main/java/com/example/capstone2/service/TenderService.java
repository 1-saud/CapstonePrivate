package com.example.capstone2.service;


import com.example.capstone2.model.Tender;
import com.example.capstone2.repository.TenderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TenderService {

    private final TenderRepository tenderRepository;

    public List<Tender> getAllTenders() {
        return tenderRepository.findAll();
    }

    public Tender getTenderById(Integer id) {
        return tenderRepository.findTenderById(id);
    }

    public boolean addTender(Tender tender) {
        tender.setCreatedAt(LocalDateTime.now());
        tenderRepository.save(tender);
        return true;
    }

    public boolean updateTender(Integer id, Tender tender) {
        Tender oldTender = tenderRepository.findTenderById(id);
        if (oldTender == null) {
            return false;
        }

        oldTender.setTitle(tender.getTitle());
        oldTender.setDescription(tender.getDescription());
        oldTender.setBudgetMin(tender.getBudgetMin());
        oldTender.setBudgetMax(tender.getBudgetMax());
        oldTender.setDeadline(tender.getDeadline());
        oldTender.setStatus(tender.getStatus());
        oldTender.setClientId(tender.getClientId());

        tenderRepository.save(oldTender);
        return true;
    }

    public boolean deleteTender(Integer id) {
        Tender tender = tenderRepository.findTenderById(id);
        if (tender == null) {
            return false;
        }
        tenderRepository.delete(tender);
        return true;
    }

    public List<Tender> getTendersByClientId(Integer clientId) {
        return tenderRepository.findTendersByClientId(clientId);
    }

    public List<Tender> getTendersByStatus(String status) {
        return tenderRepository.findTendersByStatus(status);
    }
}
