package com.example.capstone2.service;

import com.example.capstone2.dto.proposal.ProposalGenerateRequest;
import com.example.capstone2.dto.gamma.GammaGenerationRequest;
import com.example.capstone2.dto.gamma.GammaGenerationStatusResponse;
import com.example.capstone2.dto.gamma.GammaImageOptions;
import com.example.capstone2.dto.gamma.GammaTextOptions;
import com.example.capstone2.model.CompanyProfile;
import com.example.capstone2.model.Proposal;
import com.example.capstone2.model.Tender;
import com.example.capstone2.repository.CompanyProfileRepository;
import com.example.capstone2.repository.ProposalRepository;
import com.example.capstone2.repository.TenderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final TenderRepository tenderRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final GammaClientService gammaClientService;

    // ===== CRUD العادية =====

    public List<Proposal> getAllProposals() {
        return proposalRepository.findAll();
    }

    public Proposal getProposalById(Integer id) {
        return proposalRepository.findProposalById(id);
    }

    public List<Proposal> getProposalsByTenderId(Integer tenderId) {
        return proposalRepository.findProposalsByTenderId(tenderId);
    }

    public List<Proposal> getProposalsByVendorId(Integer vendorId) {
        return proposalRepository.findProposalsByVendorId(vendorId);
    }

    public List<Proposal> getProposalsByStatus(String status) {
        return proposalRepository.findProposalsByStatus(status);
    }

    public boolean addProposal(Proposal proposal) {
        LocalDateTime now = LocalDateTime.now();
        proposal.setCreatedAt(now);
        proposal.setUpdatedAt(now);
        proposalRepository.save(proposal);
        return true;
    }

    public boolean updateProposal(Integer id, Proposal proposal) {
        Proposal oldProposal = proposalRepository.findProposalById(id);
        if (oldProposal == null) {
            return false;
        }

        oldProposal.setTenderId(proposal.getTenderId());
        oldProposal.setVendorId(proposal.getVendorId());
        oldProposal.setStatus(proposal.getStatus());
        oldProposal.setTechnicalSummary(proposal.getTechnicalSummary());
        oldProposal.setFinancialSummary(proposal.getFinancialSummary());
        oldProposal.setCerebrasRequestId(proposal.getCerebrasRequestId());
        oldProposal.setGammaShareLink(proposal.getGammaShareLink());
        oldProposal.setPdfUrl(proposal.getPdfUrl());
        oldProposal.setPptxUrl(proposal.getPptxUrl());
        oldProposal.setAiProvider(proposal.getAiProvider());
        oldProposal.setUpdatedAt(LocalDateTime.now());

        proposalRepository.save(oldProposal);
        return true;
    }

    public boolean deleteProposal(Integer id) {
        Proposal proposal = proposalRepository.findProposalById(id);
        if (proposal == null) {
            return false;
        }
        proposalRepository.delete(proposal);
        return true;
    }

    // ===== الميثود الجديدة: توليد العرض عن طريق Gamma =====

    public Proposal generateProposal(ProposalGenerateRequest request) {

        // 1) تأكد أن الـ Tender موجود
        Tender tender = tenderRepository.findTenderById(request.getTenderId());
        if (tender == null) {
            return null;
        }

        // 2) تأكد أن CompanyProfile موجود لهذا الـ Vendor
        CompanyProfile profile =
                companyProfileRepository.findCompanyProfileByVendorId(request.getVendorId());
        if (profile == null) {
            return null;
        }

        // 3) نبني الـ prompt (النص اللي نرسله لـ Gamma)
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a technical and financial proposal for the following tender.\n\n");

        prompt.append("Language: ").append(request.getLanguage()).append("\n\n");

        prompt.append("Tender Details:\n");
        prompt.append("- Title: ").append(tender.getTitle()).append("\n");
        prompt.append("- Description: ").append(tender.getDescription()).append("\n");
        prompt.append("- Budget range: ").append(tender.getBudgetMin())
                .append(" - ").append(tender.getBudgetMax()).append("\n\n");

        prompt.append("Vendor Company Profile:\n");
        prompt.append("- Name: ").append(profile.getCompanyName()).append("\n");
        prompt.append("- Summary: ").append(profile.getSummary()).append("\n");
        prompt.append("- Website: ").append(profile.getWebsite()).append("\n");
        prompt.append("- Location: ").append(profile.getCity())
                .append(", ").append(profile.getCountry()).append("\n\n");

        prompt.append("Additional notes from vendor:\n");
        prompt.append(request.getNotes() == null ? "None" : request.getNotes());

        // 4) نكوّن طلب Gamma
        GammaTextOptions textOptions = new GammaTextOptions();
        textOptions.setAmount("detailed");
        textOptions.setTone("professional");
        textOptions.setAudience("Saudi government tender evaluation committee");
        textOptions.setLanguage(
                request.getLanguage().equalsIgnoreCase("Arabic") ? "ar" : "en"
        );

        GammaImageOptions imageOptions = new GammaImageOptions();
        imageOptions.setSource("aiGenerated");
        imageOptions.setStyle("professional");
        imageOptions.setModel(null);

        GammaGenerationRequest gammaReq = new GammaGenerationRequest();
        gammaReq.setInputText(prompt.toString());
        gammaReq.setTextMode("generate");
        gammaReq.setFormat("presentation");
        gammaReq.setExportAs("pdf");
        gammaReq.setTextOptions(textOptions);
        gammaReq.setImageOptions(imageOptions);
        gammaReq.setNumCards(10);
        gammaReq.setAdditionalInstructions(
                "Use a clean, modern design suitable for Saudi government tenders."
        );

        // 5) نرسل لـ Gamma وننتظر
        GammaGenerationStatusResponse gammaRes =
                gammaClientService.generateAndWait(gammaReq);

        String gammaUrl  = gammaRes.getGammaUrl();
        String pdfUrl    = null;
        String pptxUrl   = null;

        if (gammaRes.getRaw() != null) {
            Object export = gammaRes.getRaw().get("exportUrl");
            if (export != null) {
                // غالباً هذا PDF، لو حاب تضيف PPTX بعدين عدّل هنا
                pdfUrl = export.toString();
            }
        }

        // 6) ننشئ Proposal ونخزّنه في الداتابيس
        Proposal proposal = new Proposal();
        proposal.setTenderId(request.getTenderId());
        proposal.setVendorId(request.getVendorId());

        // انتبه: الـ Validation عندك يسمح فقط بـ APPROVED / REJECTED / NEEDS_CHANGES
        // فخلي الـ default "NEEDS_CHANGES" (يعني: بانتظار موافقة العميل)
        proposal.setStatus("NEEDS_CHANGES");

        proposal.setTechnicalSummary("AI-generated proposal based on tender #" + tender.getId());
        proposal.setFinancialSummary("See attached Gamma presentation / PDF.");
        proposal.setGammaShareLink(gammaUrl);
        proposal.setPdfUrl(pdfUrl);
        proposal.setPptxUrl(pptxUrl);
        proposal.setAiProvider("GAMMA");

        LocalDateTime now = LocalDateTime.now();
        proposal.setCreatedAt(now);
        proposal.setUpdatedAt(now);

        return proposalRepository.save(proposal);
    }
}
