package com.demo.bdd;

import com.demo.core.config.PlaywrightConfig;
import com.demo.core.logger.DefaultLogger;
import com.demo.utils.Constants;
import com.demo.utils.PlaywrightTools;
import com.microsoft.playwright.Page;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class Hooks extends DefaultLogger {

    protected Page page;

    @Before
    public void beforeScenario() {
        logInfo("Creating Playwright browser configuration...");
        PlaywrightConfig.createBrowserConfig(System.getProperty("playwright.browser", "chrome"));

        configLog(this.getClass().getSimpleName());
        logInfo("Opening page: " + Constants.URL);

        PlaywrightTools.openUrl(Constants.URL);
    }

    @After
    public void tearDown() {
        logInfo("Closing Playwright browser...");
        PlaywrightConfig.closeBrowser();
        logInfo("Playwright browser closed!");
    }
}
