package com.demo.pages;

import com.demo.core.logger.DefaultLogger;
import com.demo.pages.testSite.TestPage;

public class Pages extends DefaultLogger {
    /**
     * Pages
     */
    private static TestPage testPage;

    /**
     * This function return an instance of `TestPage`
     */
    public static TestPage testPage(){
        if(testPage == null) {
            testPage = new TestPage();
        }
        return testPage;
    }
}
