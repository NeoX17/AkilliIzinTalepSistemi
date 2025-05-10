package com.talenteer.izin_takip.repository;

import com.talenteer.izin_takip.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
 
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByUserId(Long userId);
} 