package com.demo.core.config;

import com.demo.utils.Constants;
import com.microsoft.playwright.*;
import com.microsoft.playwright.BrowserType.LaunchOptions;

import java.nio.file.Paths;
import java.util.List;

public class PlaywrightConfig {
    private static final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> contextThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();

    public static void createBrowserConfig(String browserType) {
        Playwright playwright = Playwright.create();
        playwrightThreadLocal.set(playwright);
        double timeoutTime = Constants.BIG_TIMEOUT * 1000;

        boolean isHeadless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        LaunchOptions options = new LaunchOptions()
                .setHeadless(isHeadless)
                .setArgs(List.of("--disable-notifications"))
                .setTimeout(timeoutTime);

        Browser browser;

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

        browserThreadLocal.set(browser);

        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setViewportSize(1440, 900)
                .setAcceptDownloads(true)
                .setIgnoreHTTPSErrors(true)
                .setRecordVideoDir(Paths.get("videos/"));

        BrowserContext context = browser.newContext(contextOptions);
        contextThreadLocal.set(context);

        Page page = context.newPage();
        page.setDefaultTimeout(timeoutTime);
        pageThreadLocal.set(page);
    }

    public static Page getPage() {
        Page page = pageThreadLocal.get();
        if (page == null) {
            throw new IllegalStateException("Page not initialized. Call createBrowserConfig() first.");
        }
        return page;
    }

    public static void setPage(Page newPage) {
        pageThreadLocal.set(newPage);
    }

    public static void closeBrowser() {
        BrowserContext context = contextThreadLocal.get();
        if (context != null) {
            context.close();
            contextThreadLocal.remove();
        }

        Browser browser = browserThreadLocal.get();
        if (browser != null) {
            browser.close();
            browserThreadLocal.remove();
        }

        Playwright playwright = playwrightThreadLocal.get();
        if (playwright != null) {
            playwright.close();
            playwrightThreadLocal.remove();
        }

        pageThreadLocal.remove();
    }
}
