package com.soham.reverselogistics.selenium;

import com.soham.reverselogistics.selenium.support.BaseSeleniumTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ManagerWorkflowJourneyTest extends BaseSeleniumTest {

    @Test
    @DisplayName("Journey 2: Manager reviews return, inspects stepper and audits status")
    public void testManagerWorkflowApprovalJourney() {
        loginAsManager();

        driver.get(baseUrl + "/returns");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table, .table")));

        List<WebElement> viewLinks = driver.findElements(By.cssSelector("a.return-code, a[href*='/returns/']"));
        String targetHref = null;
        for (WebElement link : viewLinks) {
            String href = link.getAttribute("href");
            if (href != null && href.matches(".*/returns/\\d+$")) {
                targetHref = href;
                break;
            }
        }
        assertNotNull(targetHref, "Should find at least one return request link on returns page");
        driver.get(targetHref);

        wait.until(ExpectedConditions.urlMatches(".*/returns/\\d+"));
        assertTrue(driver.getCurrentUrl().contains("/returns/"), "Should navigate to return details view");

        WebElement returnCodeElem = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("returnCode")));
        assertTrue(returnCodeElem.getText().startsWith("RR-"), "Should display Return Code starting with RR-");

        WebElement statusBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("status-badge")));
        assertNotNull(statusBadge.getText(), "Status badge should be present");

        // Verify stepper
        WebElement progressSteps = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("progressSteps")));
        assertTrue(progressSteps.isDisplayed(), "Workflow stepper must be displayed for manager review");

        captureEvidence("Journey2_ManagerApproval");
    }
}
