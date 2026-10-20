package com.soham.reverselogistics.model;

/**
 * The status workflow. Each action moves a request from one status to another
 * and can be done by one role only.
 */
public enum ReturnAction {

    APPROVE("Approve request", "bi-check2-circle", "success", Role.MANAGER,
            ReturnStatus.REQUESTED, ReturnStatus.APPROVED),
    REJECT("Reject request", "bi-x-circle", "danger", Role.MANAGER,
            ReturnStatus.REQUESTED, ReturnStatus.REJECTED),
    SCHEDULE("Schedule pickup", "bi-calendar-check", "primary", Role.MANAGER,
            ReturnStatus.APPROVED, ReturnStatus.PICKUP_SCHEDULED),
    RECEIVE("Mark as received", "bi-box-seam", "primary", Role.WAREHOUSE,
            ReturnStatus.PICKUP_SCHEDULED, ReturnStatus.RECEIVED),
    INSPECT("Record inspection", "bi-clipboard2-check", "primary", Role.WAREHOUSE,
            ReturnStatus.RECEIVED, ReturnStatus.INSPECTED),
    REFUND("Issue refund", "bi-cash-coin", "success", Role.MANAGER,
            ReturnStatus.INSPECTED, ReturnStatus.REFUNDED),
    RESTOCK("Restock item", "bi-arrow-repeat", "success", Role.WAREHOUSE,
            ReturnStatus.INSPECTED, ReturnStatus.RESTOCKED),
    DISPOSE("Dispose item", "bi-trash3", "danger", Role.WAREHOUSE,
            ReturnStatus.INSPECTED, ReturnStatus.DISPOSED);

    private final String label;
    private final String icon;
    private final String style;
    private final Role role;
    private final ReturnStatus from;
    private final ReturnStatus to;

    ReturnAction(String label, String icon, String style, Role role, ReturnStatus from, ReturnStatus to) {
        this.label = label;
        this.icon = icon;
        this.style = style;
        this.role = role;
        this.from = from;
        this.to = to;
    }

    public String getLabel() {
        return label;
    }

    public String getIcon() {
        return icon;
    }

    public String getStyle() {
        return style;
    }

    public Role getRole() {
        return role;
    }

    public ReturnStatus getFrom() {
        return from;
    }

    public ReturnStatus getTo() {
        return to;
    }

    /** Value used in the web form, for example "approve". */
    public String getParam() {
        return name().toLowerCase();
    }

    public boolean isNeedsRemarks() {
        return this == REJECT || this == INSPECT;
    }

    public boolean isNeedsPickupDate() {
        return this == SCHEDULE;
    }

    public boolean isNeedsRefundAmount() {
        return this == REFUND;
    }

    public boolean isDestructive() {
        return this == REJECT || this == DISPOSE;
    }

    public String getRemarksLabel() {
        if (this == REJECT) {
            return "Reason for rejection (required)";
        }
        if (this == INSPECT) {
            return "Inspection findings (required)";
        }
        return "Note (optional)";
    }

    public static ReturnAction fromParam(String value) {
        if (value != null) {
            for (ReturnAction action : values()) {
                if (action.getParam().equalsIgnoreCase(value.trim())) {
                    return action;
                }
            }
        }
        throw new IllegalArgumentException("Unknown action");
    }
}
