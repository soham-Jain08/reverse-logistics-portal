package com.soham.reverselogistics.repository;

import com.soham.reverselogistics.model.ReturnRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {

    List<ReturnRequest> findAllByOrderByCreatedAtDesc();
}