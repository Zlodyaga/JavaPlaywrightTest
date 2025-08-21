package com.demo.pages;

import com.demo.core.logger.DefaultLogger;
import com.demo.pages.testSite.TestPage;
import com.demo.pages.tools.MailinatorPage;

public class Pages extends DefaultLogger {
    /**
     * Pages
     */
    private static TestPage testPage;
    private static MailinatorPage mailinatorPage;

    /**
     * This function return an instance of `TestPage`
     */
    public static TestPage testPage(){
        if(testPage == null) {
            testPage = new TestPage();
        }
        return testPage;
    }

    /**
     * This function returns an instance of `MailinatorPage`
     */

    public static MailinatorPage mailinatorPage() {
        if (mailinatorPage == null) {
            mailinatorPage = new MailinatorPage();
        }
        return mailinatorPage;
    }
}
