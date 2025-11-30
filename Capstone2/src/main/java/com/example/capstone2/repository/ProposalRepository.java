package com.example.capstone2.repository;

import com.example.capstone2.model.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProposalRepository extends JpaRepository<Proposal, Integer> {

    Proposal findProposalById(Integer id);

    List<Proposal> findProposalsByTenderId(Integer tenderId);

    List<Proposal> findProposalsByVendorId(Integer vendorId);

    List<Proposal> findProposalsByStatus(String status);   // DRAFT / SUBMITTED / ...

    List<Proposal> findProposalsByTenderIdIn(List<Integer> tenderIds);
}
