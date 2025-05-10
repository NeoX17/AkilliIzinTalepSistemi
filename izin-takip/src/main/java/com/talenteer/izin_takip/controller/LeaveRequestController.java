package com.talenteer.izin_takip.controller;

import com.talenteer.izin_takip.model.LeaveRequest;
import com.talenteer.izin_takip.model.User;
import com.talenteer.izin_takip.repository.LeaveRequestRepository;
import com.talenteer.izin_takip.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leave")
public class LeaveRequestController {
    private final LeaveRequestRepository leaveRequestRepository;
    private final UserRepository userRepository;

    public LeaveRequestController(LeaveRequestRepository leaveRequestRepository, UserRepository userRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.userRepository = userRepository;
    }

    // Çalışan: Kendi izin taleplerini gör
    @GetMapping("/my")
    public List<LeaveRequest> getMyLeaves(@RequestParam String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) return List.of();
        return leaveRequestRepository.findByUserId(user.getId());
    }

    // Çalışan: Yeni izin talebi oluştur
    @PostMapping("/create")
    public LeaveRequest createLeave(@RequestBody Map<String, String> body) {
        User user = userRepository.findByUsername(body.get("username"));
        if (user == null) return null;
        LeaveRequest leave = new LeaveRequest();
        leave.setUser(user);
        leave.setStartDate(java.time.LocalDate.parse(body.get("startDate")));
        leave.setEndDate(java.time.LocalDate.parse(body.get("endDate")));
        leave.setReason(body.get("reason"));
        leave.setStatus("PENDING");
        return leaveRequestRepository.save(leave);
    }

    // İK: Tüm izin taleplerini gör
    @GetMapping("/all")
    public List<LeaveRequest> getAllLeaves() {
        return leaveRequestRepository.findAll();
    }
} 