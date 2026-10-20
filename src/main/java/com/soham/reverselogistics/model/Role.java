package com.soham.reverselogistics.model;

/** The three kinds of users of the portal. */
public enum Role {

    CUSTOMER("Customer"),
    MANAGER("Return Manager"),
    WAREHOUSE("Warehouse Staff");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
