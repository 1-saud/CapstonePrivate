package com.example.capstone2.repository;

import com.example.capstone2.model.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Integer> {

    CompanyProfile findCompanyProfileById(Integer id);

    CompanyProfile findCompanyProfileByVendorId(Integer vendorId);
}
