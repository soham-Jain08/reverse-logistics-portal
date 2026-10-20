package com.soham.reverselogistics.selenium;

import com.soham.reverselogistics.selenium.support.BaseSeleniumTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerReturnRequestJourneyTest extends BaseSeleniumTest {

    @Test
    @DisplayName("Journey 1: Customer submits return request and verifies detail page & stepper")
    public void testCustomerCreatesReturnRequestSuccessfully() {
        loginAsCustomer();

        driver.get(baseUrl + "/returns/new");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("orderNumber")));

        String testOrderNumber = "ORD-SEL-" + (System.currentTimeMillis() % 100000);
        String testProduct = "Sony WH-1000XM5 Wireless Headphones";
        String testCustomer = "John Customer";
        String testAddress = "Apt 12B, Ocean Crest, Worli, Mumbai 400018";
        String testReason = "Left ear cup audio is distorted and intermittent";

        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript("document.getElementById('orderNumber').value = arguments[0];", testOrderNumber);
        js.executeScript("document.getElementById('customerName').value = arguments[0];", testCustomer);
        js.executeScript("document.getElementById('productName').value = arguments[0];", testProduct);
        js.executeScript("document.getElementById('quantity').value = arguments[0];", "1");
        js.executeScript("document.getElementById('category').value = arguments[0];", "DEFECTIVE");
        js.executeScript("document.getElementById('reason').value = arguments[0];", testReason);
        js.executeScript("document.getElementById('pickupAddress').value = arguments[0];", testAddress);

        WebElement form = driver.findElement(By.id("returnForm"));
        form.submit();

        wait.until(ExpectedConditions.urlMatches(".*/returns/\\d+.*"));

        WebElement pageBody = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        String bodyText = pageBody.getText().toLowerCase();

        assertTrue(bodyText.contains(testOrderNumber.toLowerCase()), "Created order number should be visible on details view");
        assertTrue(bodyText.contains(testProduct.toLowerCase()), "Product name should be visible on details view");
        assertTrue(bodyText.contains("requested"), "Status should be initialized to Requested");

        captureEvidence("Journey1_ReturnCreated");
    }
}
