package com.soham.reverselogistics.model;

/** Why the customer wants to return the item. */
public enum ReturnCategory {

    DEFECTIVE("Defective or not working"),
    WRONG_ITEM("Wrong item received"),
    DAMAGED("Damaged in transit"),
    NOT_AS_DESCRIBED("Not as described"),
    SIZE_FIT("Size or fit issue"),
    CHANGED_MIND("Changed my mind"),
    OTHER("Other");

    private final String label;

    ReturnCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
