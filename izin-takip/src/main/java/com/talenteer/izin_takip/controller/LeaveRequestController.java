package com.talenteer.izin_takip.controller;

import com.talenteer.izin_takip.model.LeaveRequest;
import com.talenteer.izin_takip.model.User;
import com.talenteer.izin_takip.repository.LeaveRequestRepository;
import com.talenteer.izin_takip.repository.UserRepository;
import com.talenteer.izin_takip.service.ExcelExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leave")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class LeaveRequestController {
    private final LeaveRequestRepository leaveRequestRepository;
    private final UserRepository userRepository;
    private final ExcelExportService excelExportService;

    @Autowired
    public LeaveRequestController(LeaveRequestRepository leaveRequestRepository, UserRepository userRepository, ExcelExportService excelExportService) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.userRepository = userRepository;
        this.excelExportService = excelExportService;
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
        leave.setStatus("Bekliyor"); // "PENDING" yerine "Bekliyor" kullanacağız
        return leaveRequestRepository.save(leave);
    }

    // İK: Tüm izin taleplerini gör
    @GetMapping("/all")
    public List<LeaveRequest> getAllLeaves() {
        System.out.println("Tüm izin talepleri getiriliyor...");
        List<LeaveRequest> allLeaves = leaveRequestRepository.findAll();
        System.out.println("Toplam " + allLeaves.size() + " izin talebi bulundu.");
        for (LeaveRequest leave : allLeaves) {
            System.out.println("ID: " + leave.getId() + ", Status: " + leave.getStatus());
        }
        return allLeaves;
    }

    // İK: İzin talebini onayla
    @PostMapping("/approve/{id}")
    public LeaveRequest approveLeave(@PathVariable Long id) throws IOException {
        LeaveRequest leave = leaveRequestRepository.findById(id).orElse(null);
        if (leave == null) return null;
        leave.setStatus("onaylanan"); // "APPROVED" yerine "onaylanan" kullanacağız
        LeaveRequest updated = leaveRequestRepository.save(leave);
        // Onaylanan izinleri Excel'e kaydet
        List<LeaveRequest> approvedList = leaveRequestRepository.findAll().stream()
                .filter(l -> "onaylanan".equals(l.getStatus()))
                .collect(Collectors.toList());
        excelExportService.exportToExcel(approvedList, "approved_leaves.xlsx");
        return updated;
    }

    // İK: İzin talebini reddet
    @PostMapping("/reject/{id}")
    public LeaveRequest rejectLeave(@PathVariable Long id) throws IOException {
        LeaveRequest leave = leaveRequestRepository.findById(id).orElse(null);
        if (leave == null) return null;
        leave.setStatus("reddedilen"); // "REJECTED" yerine "reddedilen" kullanacağız
        LeaveRequest updated = leaveRequestRepository.save(leave);
        // Reddedilen izinleri Excel'e kaydet
        List<LeaveRequest> rejectedList = leaveRequestRepository.findAll().stream()
                .filter(l -> "reddedilen".equals(l.getStatus()))
                .collect(Collectors.toList());
        excelExportService.exportToExcel(rejectedList, "rejected_leaves.xlsx");
        return updated;
    }
} 