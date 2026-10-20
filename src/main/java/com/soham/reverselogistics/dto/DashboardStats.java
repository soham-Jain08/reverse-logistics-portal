package com.soham.reverselogistics.dto;

import com.soham.reverselogistics.model.ReturnRequest;
import com.soham.reverselogistics.model.ReturnStatus;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Aggregated metrics for the summary dashboard. */
public class DashboardStats {

    private long totalReturns;
    private long requestedCount;
    private long approvedCount;
    private long pickupScheduledCount;
    private long receivedCount;
    private long inspectedCount;
    private long refundedCount;
    private long restockedCount;
    private long disposedCount;
    private long rejectedCount;
    private long pendingActionCount;
    private BigDecimal totalRefunded = BigDecimal.ZERO;
    private List<ReturnRequest> recentReturns;
    private Map<ReturnStatus, Long> statusCounts = new EnumMap<>(ReturnStatus.class);

    public long getTotalReturns() {
        return totalReturns;
    }

    public void setTotalReturns(long totalReturns) {
        this.totalReturns = totalReturns;
    }

    public long getRequestedCount() {
        return requestedCount;
    }

    public void setRequestedCount(long requestedCount) {
        this.requestedCount = requestedCount;
    }

    public long getApprovedCount() {
        return approvedCount;
    }

    public void setApprovedCount(long approvedCount) {
        this.approvedCount = approvedCount;
    }

    public long getPickupScheduledCount() {
        return pickupScheduledCount;
    }

    public void setPickupScheduledCount(long pickupScheduledCount) {
        this.pickupScheduledCount = pickupScheduledCount;
    }

    public long getReceivedCount() {
        return receivedCount;
    }

    public void setReceivedCount(long receivedCount) {
        this.receivedCount = receivedCount;
    }

    public long getInspectedCount() {
        return inspectedCount;
    }

    public void setInspectedCount(long inspectedCount) {
        this.inspectedCount = inspectedCount;
    }

    public long getRefundedCount() {
        return refundedCount;
    }

    public void setRefundedCount(long refundedCount) {
        this.refundedCount = refundedCount;
    }

    public long getRestockedCount() {
        return restockedCount;
    }

    public void setRestockedCount(long restockedCount) {
        this.restockedCount = restockedCount;
    }

    public long getDisposedCount() {
        return disposedCount;
    }

    public void setDisposedCount(long disposedCount) {
        this.disposedCount = disposedCount;
    }

    public long getRejectedCount() {
        return rejectedCount;
    }

    public void setRejectedCount(long rejectedCount) {
        this.rejectedCount = rejectedCount;
    }

    public long getPendingActionCount() {
        return pendingActionCount;
    }

    public void setPendingActionCount(long pendingActionCount) {
        this.pendingActionCount = pendingActionCount;
    }

    public BigDecimal getTotalRefunded() {
        return totalRefunded;
    }

    public void setTotalRefunded(BigDecimal totalRefunded) {
        this.totalRefunded = totalRefunded == null ? BigDecimal.ZERO : totalRefunded;
    }

    public List<ReturnRequest> getRecentReturns() {
        return recentReturns;
    }

    public void setRecentReturns(List<ReturnRequest> recentReturns) {
        this.recentReturns = recentReturns;
    }

    public Map<ReturnStatus, Long> getStatusCounts() {
        return statusCounts;
    }

    public void setStatusCounts(Map<ReturnStatus, Long> statusCounts) {
        this.statusCounts = statusCounts;
    }

    public long countOf(ReturnStatus status) {
        return statusCounts.getOrDefault(status, 0L);
    }
}
