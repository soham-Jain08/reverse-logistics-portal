package com.soham.reverselogistics.dto;

import com.soham.reverselogistics.model.ReturnCategory;
import com.soham.reverselogistics.model.ReturnRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** The fields a customer can fill in when raising or editing a return request. */
public class ReturnRequestForm {

    @NotBlank(message = "Order number is required")
    @Size(max = 30, message = "Order number must be 30 characters or fewer")
    @Pattern(regexp = "^[A-Za-z0-9_/\\-]*$", message = "Use only letters, numbers and hyphens")
    private String orderNumber;

    @NotBlank(message = "Customer name is required")
    @Size(max = 80, message = "Customer name must be 80 characters or fewer")
    private String customerName;

    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name must be 100 characters or fewer")
    private String productName;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100, message = "Quantity cannot be more than 100")
    private Integer quantity;

    @NotNull(message = "Please choose a reason category")
    private ReturnCategory category;

    @NotBlank(message = "Please describe the problem")
    @Size(max = 255, message = "Description must be 255 characters or fewer")
    private String reason;

    @NotBlank(message = "Pickup address is required")
    @Size(max = 255, message = "Pickup address must be 255 characters or fewer")
    private String pickupAddress;

    public static ReturnRequestForm from(ReturnRequest r) {
        ReturnRequestForm form = new ReturnRequestForm();
        form.orderNumber = r.getOrderNumber();
        form.customerName = r.getCustomerName();
        form.productName = r.getProductName();
        form.quantity = r.getQuantity();
        form.category = r.getCategory();
        form.reason = r.getReason();
        form.pickupAddress = r.getPickupAddress();
        return form;
    }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public ReturnCategory getCategory() { return category; }
    public void setCategory(ReturnCategory category) { this.category = category; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
}
