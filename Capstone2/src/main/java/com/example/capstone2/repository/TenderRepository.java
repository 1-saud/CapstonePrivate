package com.example.capstone2.repository;

import com.example.capstone2.model.Tender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface TenderRepository extends JpaRepository<Tender, Integer> {

    Tender findTenderById(Integer id);

    List<Tender> findTendersByClientId(Integer clientId);

    List<Tender> findTendersByStatus(String status);   // OPEN / CLOSED / CANCELLED
}