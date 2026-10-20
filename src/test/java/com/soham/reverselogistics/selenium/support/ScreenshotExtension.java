package com.soham.reverselogistics.selenium.support;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotExtension implements AfterTestExecutionCallback {

    private static final String SCREENSHOT_DIR = "target/screenshots";

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        if (context.getExecutionException().isPresent()) {
            Object testInstance = context.getRequiredTestInstance();
            if (testInstance instanceof WebDriverHolder) {
                WebDriver driver = ((WebDriverHolder) testInstance).getDriver();
                if (driver != null) {
                    String className = context.getRequiredTestClass().getSimpleName();
                    String methodName = context.getRequiredTestMethod().getName();
                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String fileName = String.format("FAILURE_%s_%s_%s.png", className, methodName, timestamp);

                    captureScreenshot(driver, fileName);
                    System.err.println("[SELENIUM] Test failed! Screenshot captured at: " + SCREENSHOT_DIR + "/" + fileName);
                }
            }
        }
    }

    public static File captureScreenshot(WebDriver driver, String fileName) {
        try {
            Path targetDir = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            if (driver instanceof TakesScreenshot) {
                File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                File dest = targetDir.resolve(fileName).toFile();
                Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return dest;
            }
        } catch (IOException e) {
            System.err.println("Could not capture screenshot: " + e.getMessage());
        }
        return null;
    }
}
