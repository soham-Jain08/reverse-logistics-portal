package com.soham.reverselogistics.selenium.support;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.io.File;
import java.time.Duration;

public class DriverFactory {

    private static final String DEFAULT_BASE_URL = "http://localhost:8081";

    public static String getBaseUrl() {
        String sysProp = System.getProperty("app.url");
        if (sysProp != null && !sysProp.isBlank()) {
            return sysProp.endsWith("/") ? sysProp.substring(0, sysProp.length() - 1) : sysProp;
        }
        String env = System.getenv("APP_URL");
        if (env != null && !env.isBlank()) {
            return env.endsWith("/") ? env.substring(0, env.length() - 1) : env;
        }
        return DEFAULT_BASE_URL;
    }

    public static WebDriver createDriver() {
        File localDriver = new File("C:\\Users\\CSC\\.cache\\selenium\\chromedriver\\win64\\155.0.8059.84\\chromedriver.exe");
        if (localDriver.exists() && System.getProperty("webdriver.chrome.driver") == null) {
            System.setProperty("webdriver.chrome.driver", localDriver.getAbsolutePath());
        }

        WebDriver driver;
        String browser = System.getProperty("selenium.browser", "chrome").toLowerCase();

        try {
            if ("edge".equals(browser)) {
                EdgeOptions options = new EdgeOptions();
                options.setPageLoadStrategy(PageLoadStrategy.EAGER);
                options.addArguments("--headless=new", "--disable-gpu", "--no-sandbox",
                        "--disable-dev-shm-usage", "--disable-software-rasterizer",
                        "--disable-renderer-backgrounding", "--disable-backgrounding-occluded-windows",
                        "--window-size=1920,1080", "--remote-allow-origins=*");
                File edgeBin = new File("C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe");
                if (edgeBin.exists()) {
                    options.setBinary(edgeBin);
                }
                driver = new EdgeDriver(options);
            } else {
                ChromeOptions options = new ChromeOptions();
                options.setPageLoadStrategy(PageLoadStrategy.EAGER);
                options.addArguments("--headless=new", "--disable-gpu", "--no-sandbox",
                        "--disable-dev-shm-usage", "--disable-software-rasterizer", "--disable-extensions",
                        "--disable-renderer-backgrounding", "--disable-backgrounding-occluded-windows",
                        "--disable-features=IsolateOrigins,site-per-process",
                        "--blink-settings=imagesEnabled=false",
                        "--no-zygote", "--single-process",
                        "--window-size=1920,1080", "--remote-allow-origins=*");
                // Windows path
                File chromeBin = new File("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
                // Linux path (Docker/CI)
                File chromeBinLinux = new File("/usr/bin/google-chrome-stable");
                if (chromeBin.exists()) {
                    options.setBinary(chromeBin);
                } else if (chromeBinLinux.exists()) {
                    options.setBinary(chromeBinLinux);
                }
                driver = new ChromeDriver(options);
            }
        } catch (Exception e) {
            System.err.println("Primary browser init failed (" + browser + "), attempting alternate: " + e.getMessage());
            ChromeOptions options = new ChromeOptions();
            options.setPageLoadStrategy(PageLoadStrategy.EAGER);
            options.addArguments("--headless=new", "--disable-gpu", "--no-sandbox",
                    "--disable-dev-shm-usage", "--disable-software-rasterizer", "--disable-extensions",
                    "--disable-renderer-backgrounding", "--disable-backgrounding-occluded-windows",
                    "--disable-features=IsolateOrigins,site-per-process",
                    "--blink-settings=imagesEnabled=false",
                    "--no-zygote", "--single-process",
                    "--window-size=1920,1080", "--remote-allow-origins=*");
            File chromeBinLinuxFallback = new File("/usr/bin/google-chrome-stable");
            if (chromeBinLinuxFallback.exists()) {
                options.setBinary(chromeBinLinuxFallback);
            }
            driver = new ChromeDriver(options);
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        return driver;
    }
}
