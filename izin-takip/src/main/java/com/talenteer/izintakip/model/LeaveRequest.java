package com.talenteer.izintakip.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "leave_requests")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private String reason;
    
    private String status; // bekleyen, onaylanan, reddedilen
    
    @Column(name = "used_leave")
    private Integer usedLeave;
    
    @Column(name = "remaining_leave")
    private Integer remainingLeave;
    
    @Column(name = "request_date")
    private LocalDate requestDate;
    
    // Constructors
    public LeaveRequest() {
    }
    
    public LeaveRequest(Long id, User user, LocalDate startDate, LocalDate endDate, String reason, String status, 
                       Integer usedLeave, Integer remainingLeave, LocalDate requestDate) {
        this.id = id;
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
        this.usedLeave = usedLeave;
        this.remainingLeave = remainingLeave;
        this.requestDate = requestDate;
    }
    
    // Getter ve Setter metotları
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getUsedLeave() {
        return usedLeave;
    }

    public void setUsedLeave(Integer usedLeave) {
        this.usedLeave = usedLeave;
    }

    public Integer getRemainingLeave() {
        return remainingLeave;
    }

    public void setRemainingLeave(Integer remainingLeave) {
        this.remainingLeave = remainingLeave;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }
} 