package com.soham.reverselogistics.service;

import com.soham.reverselogistics.dto.ActionForm;
import com.soham.reverselogistics.dto.NotesForm;
import com.soham.reverselogistics.dto.ReturnRequestForm;
import com.soham.reverselogistics.model.ReturnAction;
import com.soham.reverselogistics.model.ReturnCategory;
import com.soham.reverselogistics.model.ReturnRequest;
import com.soham.reverselogistics.model.ReturnStatus;
import com.soham.reverselogistics.model.StatusHistory;
import com.soham.reverselogistics.repository.ReturnRequestRepository;
import com.soham.reverselogistics.repository.StatusHistoryRepository;
import com.soham.reverselogistics.security.CurrentUser;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** All business rules of the return workflow live here. */
@Service
public class ReturnRequestService {

    public static final int PAGE_SIZE = 8;

    private final ReturnRequestRepository repository;
    private final StatusHistoryRepository historyRepository;

    public ReturnRequestService(ReturnRequestRepository repository, StatusHistoryRepository historyRepository) {
        this.repository = repository;
        this.historyRepository = historyRepository;
    }

    // ---------------------------------------------------------------- reading

    @Transactional(readOnly = true)
    public Page<ReturnRequest> search(CurrentUser user, String q, String status, String category, int page) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
        return repository.search(scope(user), clean(q), enumName(status, ReturnStatus.class),
                enumName(category, ReturnCategory.class), pageable);
    }

    @Transactional(readOnly = true)
    public List<ReturnRequest> searchAll(CurrentUser user, String q, String status, String category) {
        Pageable pageable = PageRequest.of(0, 5000, Sort.by(Sort.Direction.DESC, "createdAt"));
        return repository.search(scope(user), clean(q), enumName(status, ReturnStatus.class),
                enumName(category, ReturnCategory.class), pageable).getContent();
    }

    @Transactional(readOnly = true)
    public ReturnRequest get(Long id, CurrentUser user) {
        ReturnRequest request = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Return request not found"));
        if (user.isCustomer() && !user.getUsername().equals(request.getCreatedBy())) {
            // customers must not even learn that other customers' requests exist
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Return request not found");
        }
        return request;
    }

    @Transactional(readOnly = true)
    public List<StatusHistory> history(Long id) {
        return historyRepository.findByRequestIdOrderByChangedAtAsc(id);
    }

    // --------------------------------------------------------------- creating

    @Transactional
    public ReturnRequest create(ReturnRequestForm form, CurrentUser user) {
        if (!user.isCustomer()) {
            throw new AccessDeniedException("Only customers can raise return requests");
        }
        ReturnRequest request = new ReturnRequest();
        apply(form, request);
        request.setStatus(ReturnStatus.REQUESTED);
        request.setCreatedBy(user.getUsername());
        ReturnRequest saved = repository.save(request);
        record(saved, null, ReturnStatus.REQUESTED, user, "Return request created");
        return saved;
    }

    // --------------------------------------------------------------- updating

    @Transactional
    public ReturnRequest updateByCustomer(Long id, ReturnRequestForm form, CurrentUser user) {
        ReturnRequest request = get(id, user);
        assertCustomerCanEdit(request, user);
        apply(form, request);
        ReturnRequest saved = repository.save(request);
        record(saved, saved.getStatus(), saved.getStatus(), user, "Request details updated by the customer");
        return saved;
    }

    public void assertCustomerCanEdit(ReturnRequest request, CurrentUser user) {
        if (!user.isCustomer() || !user.getUsername().equals(request.getCreatedBy())) {
            throw new AccessDeniedException("You cannot edit this request");
        }
        if (request.getStatus() != ReturnStatus.REQUESTED) {
            throw new IllegalStateException("Only requests that are still waiting for approval can be edited.");
        }
    }

    public boolean canCustomerEdit(ReturnRequest request, CurrentUser user) {
        return user.isCustomer() && user.getUsername().equals(request.getCreatedBy())
                && request.getStatus() == ReturnStatus.REQUESTED;
    }

    public boolean canAnnotate(ReturnRequest request, CurrentUser user) {
        return user.isStaff() && !request.getStatus().isClosed();
    }

    public boolean canPrintLabel(ReturnRequest request) {
        return request.getStatus() != ReturnStatus.REQUESTED && request.getStatus() != ReturnStatus.REJECTED;
    }

    @Transactional
    public ReturnRequest updateNotes(Long id, NotesForm form, CurrentUser user) {
        ReturnRequest request = get(id, user);
        if (!user.isStaff()) {
            throw new AccessDeniedException("Only staff can update notes");
        }
        if (request.getStatus().isClosed()) {
            throw new IllegalStateException("This request is closed, so its notes can no longer be changed.");
        }
        if (user.isManager()) {
            request.setRemarks(trimToNull(form.getRemarks()));
            if (form.getRefundAmount() != null) {
                if (form.getRefundAmount().compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Refund amount cannot be negative.");
                }
                request.setRefundAmount(form.getRefundAmount());
            }
        } else {
            request.setInspectionRemarks(trimToNull(form.getInspectionRemarks()));
        }
        ReturnRequest saved = repository.save(request);
        record(saved, saved.getStatus(), saved.getStatus(), user, "Notes updated by " + user.getRole().getLabel());
        return saved;
    }

    // --------------------------------------------------------------- workflow

    public List<ReturnAction> availableActions(ReturnRequest request, CurrentUser user) {
        List<ReturnAction> list = new ArrayList<>();
        for (ReturnAction action : ReturnAction.values()) {
            if (action.getFrom() == request.getStatus() && action.getRole() == user.getRole()) {
                list.add(action);
            }
        }
        return list;
    }

    @Transactional
    public ReturnRequest applyAction(Long id, ReturnAction action, ActionForm form, CurrentUser user) {
        ReturnRequest request = get(id, user);

        if (user.getRole() != action.getRole()) {
            throw new AccessDeniedException("Your role cannot perform this action");
        }
        if (request.getStatus() != action.getFrom()) {
            throw new IllegalStateException("This request is \"" + request.getStatus().getLabel()
                    + "\", so it cannot be changed with \"" + action.getLabel() + "\".");
        }

        String note = trimToNull(form.getRemarks());

        if (action == ReturnAction.REJECT) {
            if (note == null) {
                throw new IllegalArgumentException("Please give a reason for rejecting the request.");
            }
            request.setRemarks(note);
        } else if (action == ReturnAction.SCHEDULE) {
            LocalDate date = form.getPickupDate();
            if (date == null) {
                throw new IllegalArgumentException("Please choose a pickup date.");
            }
            if (date.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("The pickup date cannot be in the past.");
            }
            request.setPickupDate(date);
        } else if (action == ReturnAction.INSPECT) {
            if (note == null) {
                throw new IllegalArgumentException("Please record the inspection findings.");
            }
            request.setInspectionRemarks(note);
        } else if (action == ReturnAction.REFUND) {
            BigDecimal amount = form.getRefundAmount();
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("The refund amount must be greater than zero.");
            }
            request.setRefundAmount(amount);
        }

        request.setStatus(action.getTo());
        ReturnRequest saved = repository.save(request);
        record(saved, action.getFrom(), action.getTo(), user, note != null ? note : defaultNote(action, form));
        return saved;
    }

    private String defaultNote(ReturnAction action, ActionForm form) {
        if (action == ReturnAction.SCHEDULE) {
            return "Pickup scheduled for " + form.getPickupDate();
        }
        if (action == ReturnAction.REFUND) {
            return "Refund of " + form.getRefundAmount() + " issued";
        }
        return action.getLabel();
    }

    // ---------------------------------------------------------------- helpers

    private void apply(ReturnRequestForm form, ReturnRequest request) {
        request.setOrderNumber(form.getOrderNumber().trim());
        request.setCustomerName(form.getCustomerName().trim());
        request.setProductName(form.getProductName().trim());
        request.setQuantity(form.getQuantity());
        request.setCategory(form.getCategory());
        request.setReason(form.getReason().trim());
        request.setPickupAddress(form.getPickupAddress().trim());
    }

    private void record(ReturnRequest request, ReturnStatus from, ReturnStatus to, CurrentUser user, String note) {
        historyRepository.save(new StatusHistory(request.getId(), from, to, user.getUsername(),
                user.getRole(), note, LocalDateTime.now()));
    }

    private static String scope(CurrentUser user) {
        return user.isCustomer() ? user.getUsername() : "";
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private static String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private static <E extends Enum<E>> String enumName(String value, Class<E> type) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }
        try {
            return Enum.valueOf(type, value.trim().toUpperCase()).name();
        } catch (IllegalArgumentException ex) {
            return "";
        }
    }
}
