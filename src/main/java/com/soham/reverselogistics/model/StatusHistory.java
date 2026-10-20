package com.soham.reverselogistics.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/** One line of the audit trail: who changed a request, to which status, and when. */
@Entity
@Table(name = "status_history")
public class StatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long requestId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ReturnStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReturnStatus toStatus;

    @Column(nullable = false, length = 30)
    private String changedBy;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Role actorRole;

    @Column(length = 255)
    private String note;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    protected StatusHistory() {
    }

    public StatusHistory(Long requestId, ReturnStatus fromStatus, ReturnStatus toStatus,
                         String changedBy, Role actorRole, String note, LocalDateTime changedAt) {
        this.requestId = requestId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedBy = changedBy;
        this.actorRole = actorRole;
        this.note = note;
        this.changedAt = changedAt;
    }

    public String getReturnCode() {
        return "RR-" + String.format("%05d", requestId);
    }

    public Long getId() { return id; }
    public Long getRequestId() { return requestId; }
    public ReturnStatus getFromStatus() { return fromStatus; }
    public ReturnStatus getToStatus() { return toStatus; }
    public String getChangedBy() { return changedBy; }
    public Role getActorRole() { return actorRole; }
    public String getNote() { return note; }
    public LocalDateTime getChangedAt() { return changedAt; }
}
