package com.demo.pages;

import com.demo.core.allure.AllureLogger;
import com.demo.core.config.PlaywrightConfig;
import com.microsoft.playwright.Page;

public class Pages extends AllureLogger {
    /**
     * Pages
     */
    private static LoginPage loginPage;
    /**
     * This function return an instance of `NavigationPage`
     */
    public static LoginPage loginPage(){
        if(loginPage == null) {
            loginPage = new LoginPage(PlaywrightConfig.getPage());
        }
        return loginPage;
    }


}