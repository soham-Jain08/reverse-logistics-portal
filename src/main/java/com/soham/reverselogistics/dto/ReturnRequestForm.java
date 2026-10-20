package com.soham.reverselogistics.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReturnRequestForm {

    @NotBlank(message = "Order number is required")
    @Size(max = 30, message = "Order number must be 30 characters or fewer")
    private String orderNumber;

    @NotBlank(message = "Customer name is required")
    @Size(max = 80, message = "Customer name must be 80 characters or fewer")
    private String customerName;

    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name must be 100 characters or fewer")
    private String productName;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotBlank(message = "Reason is required")
    @Size(max = 255, message = "Reason must be 255 characters or fewer")
    private String reason;

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}