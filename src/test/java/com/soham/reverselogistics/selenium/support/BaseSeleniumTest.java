package com.soham.reverselogistics.selenium.support;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.List;

@ExtendWith(ScreenshotExtension.class)
public abstract class BaseSeleniumTest implements WebDriverHolder {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected String baseUrl;

    @Override
    public WebDriver getDriver() {
        return driver;
    }

    @BeforeEach
    public void setUp() {
        baseUrl = DriverFactory.getBaseUrl();
        driver = DriverFactory.createDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception ignored) {
            }
        }
    }

    protected void login(String username, String password) {
        driver.get(baseUrl + "/login");
        WebElement userInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passInput = driver.findElement(By.id("password"));
        userInput.clear();
        userInput.sendKeys(username);
        passInput.clear();
        passInput.sendKeys(password);

        WebElement form = driver.findElement(By.cssSelector("form[action*='/login']"));
        form.submit();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/dashboard"),
                ExpectedConditions.urlContains("/returns"),
                ExpectedConditions.not(ExpectedConditions.urlContains("/login"))
        ));
    }

    protected void loginAsCustomer() {
        login("customer1", "Customer@123");
    }

    protected void loginAsManager() {
        login("manager1", "Manager@123");
    }

    protected void loginAsWarehouse() {
        login("warehouse1", "Warehouse@123");
    }

    protected WebElement waitFor(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected File captureEvidence(String journeyName) {
        String fileName = String.format("JOURNEY_%s_%d.png", journeyName, System.currentTimeMillis());
        return ScreenshotExtension.captureScreenshot(driver, fileName);
    }
}
