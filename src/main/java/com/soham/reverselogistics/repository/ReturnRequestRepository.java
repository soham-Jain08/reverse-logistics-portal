package com.soham.reverselogistics.repository;

import com.soham.reverselogistics.model.ReturnRequest;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {

    /**
     * Search with optional filters. An empty string means "do not filter on this".
     * createdBy limits a customer to their own requests.
     */
    @Query("select r from ReturnRequest r where "
            + "(:createdBy = '' or r.createdBy = :createdBy) "
            + "and (:status = '' or str(r.status) = :status) "
            + "and (:category = '' or str(r.category) = :category) "
            + "and (:q = '' or lower(r.orderNumber) like concat('%', lower(:q), '%') "
            + "or lower(r.customerName) like concat('%', lower(:q), '%') "
            + "or lower(r.productName) like concat('%', lower(:q), '%'))")
    Page<ReturnRequest> search(@Param("createdBy") String createdBy,
                               @Param("status") String status,
                               @Param("category") String category,
                               @Param("q") String q,
                               Pageable pageable);

    List<ReturnRequest> findByCreatedBy(String createdBy);
}
