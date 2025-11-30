package com.example.capstone2.service;

import com.example.capstone2.dto.gamma.GammaGenerationRequest;
import com.example.capstone2.dto.gamma.GammaGenerationStatusResponse;
import com.example.capstone2.dto.gamma.GammaImageOptions;
import com.example.capstone2.dto.gamma.GammaTextOptions;
import com.example.capstone2.dto.proposal.ProposalDecisionRequest;
import com.example.capstone2.dto.proposal.ProposalGenerateRequest;
import com.example.capstone2.dto.proposal.ProposalSubmitRequest;
import com.example.capstone2.model.CompanyProfile;
import com.example.capstone2.model.Proposal;
import com.example.capstone2.model.Tender;
import com.example.capstone2.model.TenderRequirement;
import com.example.capstone2.repository.CompanyProfileRepository;
import com.example.capstone2.repository.ProposalRepository;
import com.example.capstone2.repository.TenderRepository;
import com.example.capstone2.repository.TenderRequirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final TenderRepository tenderRepository;
    private final TenderRequirementRepository tenderRequirementRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final GammaClientService gammaClientService;

    // ===== Basic CRUD =====

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

    /**
     * Returns every proposal that targets tenders owned by the specified client.
     */
    public List<Proposal> getProposalsForClient(Integer clientId) {
        List<Tender> clientTenders = tenderRepository.findTendersByClientId(clientId);
        if (clientTenders == null || clientTenders.isEmpty()) {
            return List.of();
        }

        List<Integer> tenderIds = clientTenders.stream()
                .map(Tender::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (tenderIds.isEmpty()) {
            return List.of();
        }

        return proposalRepository.findProposalsByTenderIdIn(tenderIds);
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
        oldProposal.setLanguage(proposal.getLanguage());
        oldProposal.setVendorNotes(proposal.getVendorNotes());
        oldProposal.setSubmissionMessage(proposal.getSubmissionMessage());
        oldProposal.setTechnicalSummary(proposal.getTechnicalSummary());
        oldProposal.setFinancialSummary(proposal.getFinancialSummary());
        oldProposal.setCerebrasRequestId(proposal.getCerebrasRequestId());
        oldProposal.setGammaShareLink(proposal.getGammaShareLink());
        oldProposal.setPdfUrl(proposal.getPdfUrl());
        oldProposal.setPptxUrl(proposal.getPptxUrl());
        oldProposal.setAiProvider(proposal.getAiProvider());
        oldProposal.setClientFeedback(proposal.getClientFeedback());
        oldProposal.setDecidedByClientId(proposal.getDecidedByClientId());
        oldProposal.setSubmittedAt(proposal.getSubmittedAt());
        oldProposal.setDecidedAt(proposal.getDecidedAt());
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

    // ===== AI generation =====

    public Proposal generateProposal(ProposalGenerateRequest request) {

        Tender tender = tenderRepository.findTenderById(request.getTenderId());
        if (tender == null) {
            throw new IllegalStateException("Tender not found");
        }

        CompanyProfile profile =
                companyProfileRepository.findCompanyProfileByVendorId(request.getVendorId());
        if (profile == null) {
            throw new IllegalStateException("Company profile not found for vendor");
        }

        List<TenderRequirement> requirements =
                tenderRequirementRepository.findTenderRequirementsByTenderId(tender.getId());

        String prompt = buildPrompt(tender, requirements, profile, request);
        GammaGenerationRequest gammaRequest = buildGammaRequest(prompt, request.getLanguage());
        GammaGenerationStatusResponse gammaResponse = gammaClientService.generateAndWait(gammaRequest);

        if (gammaResponse == null || gammaResponse.getGammaUrl() == null
                || !"completed".equalsIgnoreCase(gammaResponse.getStatus())) {
            throw new IllegalStateException("Gamma did not return a completed presentation");
        }

        Proposal proposal = new Proposal();
        proposal.setTenderId(request.getTenderId());
        proposal.setVendorId(request.getVendorId());
        proposal.setStatus("DRAFT");
        proposal.setLanguage(request.getLanguage());
        proposal.setVendorNotes(normalize(request.getNotes()));
        proposal.setSubmissionMessage(null);
        proposal.setTechnicalSummary("AI-generated proposal based on tender #" + tender.getId());
        proposal.setFinancialSummary("Please review the attached Gamma presentation and exports.");
        proposal.setCerebrasRequestId(gammaResponse.getGenerationId());
        proposal.setAiProvider("GAMMA");
        proposal.setClientFeedback(null);
        proposal.setDecidedByClientId(null);
        proposal.setSubmittedAt(null);
        proposal.setDecidedAt(null);

        applyGammaExports(proposal, gammaResponse);

        LocalDateTime now = LocalDateTime.now();
        proposal.setCreatedAt(now);
        proposal.setUpdatedAt(now);

        return proposalRepository.save(proposal);
    }

    // ===== Vendor & client workflow =====

    public Proposal submitProposal(Integer proposalId, ProposalSubmitRequest request) {
        Proposal proposal = requireProposal(proposalId);

        if (!proposal.getVendorId().equals(request.getVendorId())) {
            throw new IllegalStateException("Vendor does not own this proposal");
        }

        String currentStatus = proposal.getStatus();
        if (!"DRAFT".equals(currentStatus) && !"NEEDS_CHANGES".equals(currentStatus)) {
            throw new IllegalStateException("Proposal must be DRAFT or NEEDS_CHANGES before submitting");
        }

        LocalDateTime now = LocalDateTime.now();
        proposal.setStatus("SUBMITTED");
        proposal.setSubmissionMessage(normalize(request.getMessage()));
        proposal.setSubmittedAt(now);
        proposal.setUpdatedAt(now);
        proposal.setDecidedAt(null);
        proposal.setDecidedByClientId(null);

        return proposalRepository.save(proposal);
    }

    public Proposal approveProposal(Integer proposalId, ProposalDecisionRequest request) {
        return applyClientDecision(proposalId, request, "APPROVED");
    }

    public Proposal rejectProposal(Integer proposalId, ProposalDecisionRequest request) {
        return applyClientDecision(proposalId, request, "REJECTED");
    }

    public Proposal requestChanges(Integer proposalId, ProposalDecisionRequest request) {
        return applyClientDecision(proposalId, request, "NEEDS_CHANGES");
    }

    private Proposal applyClientDecision(Integer proposalId, ProposalDecisionRequest request, String newStatus) {
        Proposal proposal = requireProposal(proposalId);

        Tender tender = tenderRepository.findTenderById(proposal.getTenderId());
        if (tender == null || !tender.getClientId().equals(request.getClientId())) {
            throw new IllegalStateException("Client does not own this tender");
        }

        if (!"SUBMITTED".equals(proposal.getStatus())) {
            throw new IllegalStateException("Proposal must be SUBMITTED for client decisions");
        }

        LocalDateTime now = LocalDateTime.now();
        proposal.setStatus(newStatus);
        proposal.setClientFeedback(normalize(request.getNote()));
        proposal.setDecidedByClientId(request.getClientId());
        proposal.setDecidedAt(now);
        proposal.setUpdatedAt(now);

        return proposalRepository.save(proposal);
    }

    private Proposal requireProposal(Integer proposalId) {
        Proposal proposal = proposalRepository.findProposalById(proposalId);
        if (proposal == null) {
            throw new IllegalStateException("Proposal not found");
        }
        return proposal;
    }

    private String buildPrompt(Tender tender,
                               List<TenderRequirement> requirements,
                               CompanyProfile profile,
                               ProposalGenerateRequest request) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Generate a technical and financial proposal for the following tender.\n\n");
        prompt.append("Language: ").append(request.getLanguage()).append("\n\n");

        prompt.append("Tender Details:\n");
        prompt.append("- Title: ").append(tender.getTitle()).append("\n");
        prompt.append("- Description: ").append(tender.getDescription()).append("\n");
        prompt.append("- Budget range: ")
                .append(tender.getBudgetMin()).append(" - ").append(tender.getBudgetMax()).append("\n");
        prompt.append("- Deadline: ").append(tender.getDeadline()).append("\n\n");

        if (requirements != null && !requirements.isEmpty()) {
            prompt.append("Tender Requirements (priority in brackets):\n");
            for (TenderRequirement requirement : requirements) {
                prompt.append("- [").append(requirement.getPriority()).append("] ")
                        .append(requirement.getRequirementText()).append("\n");
            }
            prompt.append("\n");
        }

        prompt.append("Vendor Company Profile:\n");
        prompt.append("- Name: ").append(profile.getCompanyName()).append("\n");
        prompt.append("- Summary: ").append(profile.getSummary()).append("\n");
        prompt.append("- Website: ").append(profile.getWebsite()).append("\n");
        prompt.append("- Location: ").append(profile.getCity()).append(", ")
                .append(profile.getCountry()).append("\n\n");

        prompt.append("Additional notes from vendor:\n");
        String notes = normalize(request.getNotes());
        prompt.append(notes == null ? "None" : notes);

        return prompt.toString();
    }

    private GammaGenerationRequest buildGammaRequest(String prompt, String language) {
        GammaTextOptions textOptions = new GammaTextOptions();
        textOptions.setAmount("detailed");
        textOptions.setTone("professional");
        textOptions.setAudience("Saudi government tender evaluation committee");
        textOptions.setLanguage(resolveGammaLanguage(language));

        GammaImageOptions imageOptions = new GammaImageOptions();
        imageOptions.setSource("aiGenerated");
        imageOptions.setStyle("professional");
        imageOptions.setModel(null);

        GammaGenerationRequest request = new GammaGenerationRequest();
        request.setInputText(prompt);
        request.setTextMode("generate");
        request.setFormat("presentation");
        request.setExportAs("pdf");
        request.setTextOptions(textOptions);
        request.setImageOptions(imageOptions);
        request.setNumCards(10);
        request.setAdditionalInstructions(
                "Use a clean, modern design suitable for Saudi government tenders."
        );
        return request;
    }

    private void applyGammaExports(Proposal proposal, GammaGenerationStatusResponse response) {
        proposal.setGammaShareLink(response.getGammaUrl());

        Map<String, Object> raw = response.getRaw();
        if (raw == null) {
            proposal.setPdfUrl(null);
            proposal.setPptxUrl(null);
            return;
        }

        String pdfUrl = extractLink(raw, "pdf");
        if (pdfUrl == null && raw.get("exportUrl") != null) {
            pdfUrl = raw.get("exportUrl").toString();
        }
        proposal.setPdfUrl(pdfUrl);
        proposal.setPptxUrl(extractLink(raw, "pptx"));
    }

    @SuppressWarnings("unchecked")
    private String extractLink(Map<String, Object> raw, String format) {
        if (raw == null || format == null) {
            return null;
        }

        Object direct = raw.get(format + "Url");
        if (direct != null) {
            return direct.toString();
        }

        Object exports = raw.get("exports");
        if (exports instanceof Map<?, ?> exportMap) {
            Object value = ((Map<?, ?>) exportMap).get(format);
            if (value != null) {
                return value.toString();
            }
        }

        if (exports instanceof List<?> exportList) {
            for (Object item : exportList) {
                if (item instanceof Map<?, ?> entry) {
                    Object entryFormat = entry.get("format");
                    if (entryFormat != null && format.equalsIgnoreCase(entryFormat.toString())) {
                        Object url = entry.get("url");
                        if (url == null) {
                            url = entry.get("downloadUrl");
                        }
                        if (url != null) {
                            return url.toString();
                        }
                    }
                }
            }
        }

        Object fallback = raw.get(format);
        return fallback == null ? null : fallback.toString();
    }

    private String resolveGammaLanguage(String language) {
        if (language == null) {
            return "en";
        }
        String normalized = language.trim().toLowerCase();
        return normalized.startsWith("ar") ? "ar" : "en";
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
