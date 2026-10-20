package com.soham.reverselogistics.repository;

import com.soham.reverselogistics.model.ReturnCategory;
import com.soham.reverselogistics.model.ReturnRequest;
import com.soham.reverselogistics.model.ReturnStatus;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {

    /**
     * Search with optional filters.
     * null status/category means do not filter on that field.
     * createdBy limits a customer to their own requests.
     */
    @Query("select r from ReturnRequest r where "
            + "(:createdBy = '' or r.createdBy = :createdBy) "
            + "and (:status is null or r.status = :status) "
            + "and (:category is null or r.category = :category) "
            + "and (:q = '' or lower(r.orderNumber) like concat('%', lower(:q), '%') "
            + "or lower(r.customerName) like concat('%', lower(:q), '%') "
            + "or lower(r.productName) like concat('%', lower(:q), '%'))")
    Page<ReturnRequest> search(@Param("createdBy") String createdBy,
                               @Param("status") ReturnStatus status,
                               @Param("category") ReturnCategory category,
                               @Param("q") String q,
                               Pageable pageable);

    List<ReturnRequest> findByCreatedBy(String createdBy);

    long countByCreatedBy(String createdBy);

    long countByStatus(ReturnStatus status);

    long countByStatusAndCreatedBy(ReturnStatus status, String createdBy);

    List<ReturnRequest> findTop6ByOrderByCreatedAtDesc();

    List<ReturnRequest> findTop6ByCreatedByOrderByCreatedAtDesc(String createdBy);

    @Query("select coalesce(sum(r.refundAmount), 0) from ReturnRequest r where r.status = com.soham.reverselogistics.model.ReturnStatus.REFUNDED")
    BigDecimal sumRefundedAmount();

    @Query("select coalesce(sum(r.refundAmount), 0) from ReturnRequest r where r.status = com.soham.reverselogistics.model.ReturnStatus.REFUNDED and r.createdBy = :createdBy")
    BigDecimal sumRefundedAmountByCreatedBy(@Param("createdBy") String createdBy);
}
