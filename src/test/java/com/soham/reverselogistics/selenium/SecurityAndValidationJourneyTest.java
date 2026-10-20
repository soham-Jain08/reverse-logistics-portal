package com.soham.reverselogistics.selenium;

import com.soham.reverselogistics.selenium.support.BaseSeleniumTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SecurityAndValidationJourneyTest extends BaseSeleniumTest {

    @Test
    @DisplayName("Journey 5A: Unauthenticated access is redirected to login")
    public void testUnauthenticatedAccessRedirectsToLogin() {
        driver.get(baseUrl + "/returns");
        wait.until(ExpectedConditions.urlContains("/login"));

        assertTrue(driver.getCurrentUrl().contains("/login"), "Unauthenticated access must redirect to /login");
        WebElement userInput = driver.findElement(By.id("username"));
        assertTrue(userInput.isDisplayed(), "Login form username field should be visible");

        captureEvidence("Journey5A_AuthRedirect");
    }

    @Test
    @DisplayName("Journey 5B: Invalid credentials display error alert")
    public void testInvalidLoginDisplaysErrorAlert() {
        driver.get(baseUrl + "/login");
        WebElement userInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passInput = driver.findElement(By.id("password"));

        userInput.sendKeys("invalid_user");
        passInput.sendKeys("wrong_password");
        driver.findElement(By.id("loginBtn")).click();

        wait.until(ExpectedConditions.urlContains("error"));
        assertTrue(driver.getCurrentUrl().contains("error"), "URL should indicate error parameter on bad credentials");

        WebElement body = driver.findElement(By.tagName("body"));
        assertTrue(body.getText().toLowerCase().contains("wrong") ||
                   body.getText().toLowerCase().contains("invalid") ||
                   body.getText().toLowerCase().contains("error"),
                   "Error banner or alert should inform user of invalid login");

        captureEvidence("Journey5B_InvalidLogin");
    }

    @Test
    @DisplayName("Journey 5C: Form validation blocks submission on missing required fields")
    public void testFormValidationRejectsEmptySubmission() {
        loginAsCustomer();

        driver.get(baseUrl + "/returns/new");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("orderNumber")));

        // Empty out fields and submit
        driver.findElement(By.id("orderNumber")).clear();
        driver.findElement(By.id("customerName")).clear();
        driver.findElement(By.id("productName")).clear();
        driver.findElement(By.id("reason")).clear();
        driver.findElement(By.id("pickupAddress")).clear();

        WebElement form = driver.findElement(By.id("returnForm"));
        form.submit();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("invalid-feedback")));
        List<WebElement> errors = driver.findElements(By.className("invalid-feedback"));
        assertFalse(errors.isEmpty(), "Validation error messages should be displayed on empty submission");

        captureEvidence("Journey5C_ValidationBlocked");
    }
}
