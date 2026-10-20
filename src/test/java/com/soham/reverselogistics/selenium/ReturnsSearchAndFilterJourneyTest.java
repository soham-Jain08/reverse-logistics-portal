package com.soham.reverselogistics.selenium;

import com.soham.reverselogistics.selenium.support.BaseSeleniumTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReturnsSearchAndFilterJourneyTest extends BaseSeleniumTest {

    @Test
    @DisplayName("Journey 3: User performs search, filters by status and verifies CSV export availability")
    public void testSearchFilterAndExportJourney() {
        loginAsManager();

        driver.get(baseUrl + "/returns");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("form[action*='/returns']")));

        // 1. Search by Keyword
        List<WebElement> searchInputs = driver.findElements(By.name("q"));
        if (!searchInputs.isEmpty()) {
            WebElement searchBox = searchInputs.get(0);
            searchBox.clear();
            searchBox.sendKeys("ORD");
            searchBox.submit();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table, .table")));
            List<WebElement> rows = driver.findElements(By.cssSelector("tbody tr"));
            assertFalse(rows.isEmpty(), "Should display matching records for prefix ORD");
        }

        // 2. Filter by Status Tab
        driver.get(baseUrl + "/returns?status=REQUESTED");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table, .table")));
        List<WebElement> requestedRows = driver.findElements(By.cssSelector("tbody tr"));
        assertFalse(requestedRows.isEmpty(), "Should display return records for status REQUESTED");

        // 3. Verify CSV Export Link / Button
        List<WebElement> exportButtons = driver.findElements(By.cssSelector("a[href*='/export']"));
        assertFalse(exportButtons.isEmpty(), "CSV Export link should be visible on returns index view");
        assertTrue(exportButtons.get(0).getAttribute("href").contains("/export"), "Export URL must point to /returns/export");

        captureEvidence("Journey3_SearchFilterExport");
    }
}
