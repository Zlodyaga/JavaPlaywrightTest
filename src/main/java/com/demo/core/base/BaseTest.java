package com.demo.core.base;

import com.demo.core.allure.AllureLogger;
import com.demo.core.config.PlaywrightConfig;
import com.demo.utils.Constants;
import com.microsoft.playwright.Page;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

@Listeners({TestListener.class})
public class BaseTest extends AllureLogger {

    protected Page page;

    @BeforeMethod(alwaysRun = true, description = "Opening web browser...")
    public void setUp() {
        logInfo("Creating Playwright browser configuration...");
        PlaywrightConfig.createBrowserConfig(System.getProperty("playwright.browser", "chrome"));

        configLog(this.getClass().getSimpleName());
        logInfo("Opening page: " + Constants.URL);

        page = PlaywrightConfig.getPage();
        page.navigate(Constants.URL);
    }

    @AfterMethod(alwaysRun = true, description = "Closing web browser...")
    public void tearDown(ITestResult result) {
        logInfo("Closing Playwright browser...");
        PlaywrightConfig.closeBrowser();
        logInfo("Playwright browser closed!");
    }
}
