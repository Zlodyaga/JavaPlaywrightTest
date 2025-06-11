package com.demo.pages.Ithillel;

import com.demo.core.allure.AllureLogger;
import com.demo.core.config.PlaywrightConfig;
import com.microsoft.playwright.Page;

public class IthillelPages extends AllureLogger {
    /**
     * Pages
     */
    private static HomePage homePage;


    private static final Page pwPage = PlaywrightConfig.getPage();

    /**
     * This function return an instance of `NavigationPage`
     */

    public static HomePage homePage(){
        if(homePage == null) {
            homePage = new HomePage(pwPage);
        }
        return homePage;
    }


}