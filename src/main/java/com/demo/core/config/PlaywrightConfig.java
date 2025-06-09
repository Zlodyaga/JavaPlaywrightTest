package com.demo.core.config;

import com.microsoft.playwright.*;
import com.microsoft.playwright.BrowserType.LaunchOptions;

import java.nio.file.Paths;
import java.util.List;

public class PlaywrightConfig {
    public static Playwright playwright;
    public static Browser browser;
    public static BrowserContext context;
    public static Page page;

    public static void createBrowserConfig(String browserType) {
        playwright = Playwright.create();

        boolean isHeadless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        LaunchOptions options = new LaunchOptions()
                .setHeadless(isHeadless)
                .setArgs(List.of("--disable-notifications"))
                .setTimeout(18000);

        switch (browserType.toLowerCase()) {
            case "chrome":
                options.setChannel("chrome");
                browser = playwright.chromium().launch(options);
                break;

            case "firefox":
                browser = playwright.firefox().launch(options);
                break;

            default:
                throw new IllegalArgumentException("Unsupported browser: " + browserType);
        }

        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setViewportSize(1440, 900)
                .setAcceptDownloads(true)
                .setIgnoreHTTPSErrors(true)
                .setRecordVideoDir(Paths.get("videos/"));

        context = browser.newContext(contextOptions);

        page = context.newPage();
        page.setDefaultTimeout(18000);
    }

    public static Page getPage() {
        if (page == null) {
            throw new IllegalStateException("Browser not initialized. Call createBrowserConfig() first.");
        }
        return page;
    }

    public static void setPage(Page newPage) {
        page = newPage;
    }

    public static void closeBrowser() {
        if (context != null) {
            context.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}
