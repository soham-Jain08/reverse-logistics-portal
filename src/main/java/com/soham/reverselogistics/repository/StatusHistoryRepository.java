package com.soham.reverselogistics.repository;

import com.soham.reverselogistics.model.StatusHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Long> {

    List<StatusHistory> findByRequestIdOrderByChangedAtAsc(Long requestId);

    List<StatusHistory> findTop8ByOrderByChangedAtDesc();
}
