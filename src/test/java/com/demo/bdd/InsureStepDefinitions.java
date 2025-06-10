package com.demo.bdd;

import com.demo.core.allure.AllureLogger;
import com.demo.pages.Pages;
import com.demo.utils.Constants;
import com.demo.utils.PlaywrightTools;
import io.cucumber.java.en.When;
import org.junit.Assert;

public class InsureStepDefinitions extends AllureLogger {

    @When("Open login page")
    public void openLoginPage() {
        PlaywrightTools.openUrl(Constants.URL_LOGIN);
    }

    @When("Type email {string}")
    public void typeEmail(String value){
        Pages.loginPage().typeEmail(value);
        Assert.assertTrue("Should be true", false);
    }
}
