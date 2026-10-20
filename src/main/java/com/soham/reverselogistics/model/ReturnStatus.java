package com.soham.reverselogistics.model;

/** The stages a return request goes through. */
public enum ReturnStatus {

    REQUESTED("Requested", 1),
    APPROVED("Approved", 2),
    REJECTED("Rejected", 1),
    PICKUP_SCHEDULED("Pickup scheduled", 3),
    RECEIVED("Received", 4),
    INSPECTED("Inspected", 5),
    REFUNDED("Refunded", 6),
    RESTOCKED("Restocked", 6),
    DISPOSED("Disposed", 6);

    private final String label;
    private final int step;

    ReturnStatus(String label, int step) {
        this.label = label;
        this.step = step;
    }

    public String getLabel() {
        return label;
    }

    /** Position on the progress bar (1 to 6). */
    public int getStep() {
        return step;
    }

    /** A closed request cannot change any more. */
    public boolean isClosed() {
        return this == REJECTED || this == REFUNDED || this == RESTOCKED || this == DISPOSED;
    }
}
