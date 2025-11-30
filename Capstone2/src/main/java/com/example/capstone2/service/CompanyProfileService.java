package com.example.capstone2.service;


import com.example.capstone2.model.CompanyProfile;
import com.example.capstone2.repository.CompanyProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyProfileService {

    private final CompanyProfileRepository companyProfileRepository;

    public List<CompanyProfile> getAllCompanyProfiles() {
        return companyProfileRepository.findAll();
    }

    public CompanyProfile getCompanyProfileById(Integer id) {
        return companyProfileRepository.findCompanyProfileById(id);
    }

    public CompanyProfile getCompanyProfileByVendorId(Integer vendorId) {
        return companyProfileRepository.findCompanyProfileByVendorId(vendorId);
    }

    public boolean addCompanyProfile(CompanyProfile companyProfile) {
        CompanyProfile existing = companyProfileRepository.findCompanyProfileByVendorId(companyProfile.getVendorId());
        if (existing != null) {
            return false;
        }

        companyProfile.setCreatedAt(LocalDateTime.now());
        companyProfileRepository.save(companyProfile);
        return true;
    }

    public boolean updateCompanyProfile(Integer id, CompanyProfile companyProfile) {
        CompanyProfile oldProfile = companyProfileRepository.findCompanyProfileById(id);
        if (oldProfile == null) {
            return false;
        }

        oldProfile.setVendorId(companyProfile.getVendorId());
        oldProfile.setCompanyName(companyProfile.getCompanyName());
        oldProfile.setSummary(companyProfile.getSummary());
        oldProfile.setWebsite(companyProfile.getWebsite());
        oldProfile.setCountry(companyProfile.getCountry());
        oldProfile.setCity(companyProfile.getCity());

        companyProfileRepository.save(oldProfile);
        return true;
    }

    public boolean deleteCompanyProfile(Integer id) {
        CompanyProfile profile = companyProfileRepository.findCompanyProfileById(id);
        if (profile == null) {
            return false;
        }
        companyProfileRepository.delete(profile);
        return true;
    }
}
