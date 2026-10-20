package com.soham.reverselogistics.selenium;

import com.soham.reverselogistics.selenium.support.BaseSeleniumTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DashboardNavigationJourneyTest extends BaseSeleniumTest {

    @Test
    @DisplayName("Journey 4: Manager views Executive Dashboard KPI cards and interacts with Action Queues")
    public void testExecutiveDashboardMetricsAndNavigation() {
        loginAsManager();

        driver.get(baseUrl + "/dashboard");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));

        WebElement pageHeading = driver.findElement(By.tagName("h1"));
        assertTrue(pageHeading.getText().toLowerCase().contains("dashboard") ||
                   pageHeading.getText().toLowerCase().contains("overview") ||
                   driver.getTitle().toLowerCase().contains("dashboard"),
                   "Page heading or title must indicate dashboard view");

        // Verify KPI Cards / summary stat blocks
        List<WebElement> statCards = driver.findElements(By.cssSelector(".card, .stat-card, [class*='metric']"));
        assertTrue(statCards.size() >= 3, "Dashboard should render at least 3 metric/KPI cards");

        // Verify Quick Action queues or recent activity list
        List<WebElement> queueLinks = driver.findElements(By.cssSelector("a[href*='/returns']"));
        assertFalse(queueLinks.isEmpty(), "Dashboard should provide navigation links to return requests or queues");

        // Navigate via first return link from dashboard
        WebElement firstLink = queueLinks.get(0);
        String href = firstLink.getAttribute("href");
        driver.get(href);

        wait.until(ExpectedConditions.urlContains("/returns"));
        assertTrue(driver.getCurrentUrl().contains("/returns"), "Should navigate from dashboard directly into return workflow");

        captureEvidence("Journey4_DashboardKPIs");
    }
}
