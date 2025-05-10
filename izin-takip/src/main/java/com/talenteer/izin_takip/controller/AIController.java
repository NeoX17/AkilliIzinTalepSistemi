package com.talenteer.izin_takip.controller;

import com.talenteer.izin_takip.model.LeaveRequest;
import com.talenteer.izin_takip.repository.LeaveRequestRepository;
import com.talenteer.izin_takip.service.OpenAIService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    private final LeaveRequestRepository leaveRequestRepository;
    private final OpenAIService openAIService;

    public AIController(LeaveRequestRepository leaveRequestRepository, OpenAIService openAIService) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.openAIService = openAIService;
    }

    @GetMapping("/analyze")
    public String analyzeAllLeaves() {
        List<String> requests = leaveRequestRepository.findAll()
            .stream()
            .map(LeaveRequest::getReason)
            .toList();
        return openAIService.analyzeLeaves(requests);
    }
} 