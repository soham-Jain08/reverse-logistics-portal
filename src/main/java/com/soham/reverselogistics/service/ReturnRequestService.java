package com.soham.reverselogistics.service;

import com.soham.reverselogistics.dto.ReturnRequestForm;
import com.soham.reverselogistics.model.ReturnRequest;
import com.soham.reverselogistics.model.ReturnStatus;
import com.soham.reverselogistics.repository.ReturnRequestRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReturnRequestService {

    private final ReturnRequestRepository repository;

    public ReturnRequestService(ReturnRequestRepository repository) {
        this.repository = repository;
    }

    public ReturnRequest create(ReturnRequestForm form, String username) {
        ReturnRequest request = new ReturnRequest();
        request.setOrderNumber(form.getOrderNumber().trim());
        request.setCustomerName(form.getCustomerName().trim());
        request.setProductName(form.getProductName().trim());
        request.setQuantity(form.getQuantity());
        request.setReason(form.getReason().trim());
        request.setStatus(ReturnStatus.REQUESTED);
        request.setCreatedBy(username);
        return repository.save(request);
    }

    public List<ReturnRequest> findAllNewestFirst() {
        return repository.findAllByOrderByCreatedAtDesc();
    }
}