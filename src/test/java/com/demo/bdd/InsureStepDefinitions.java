package com.demo.bdd;

import com.demo.core.config.PlaywrightConfig;
import com.demo.core.logger.DefaultLogger;
import com.demo.pages.Pages;
import com.demo.utils.Constants;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.junit.Assert;

public class InsureStepDefinitions extends DefaultLogger {

    @When("Open login page")
    public void openLoginPage() {
        PlaywrightConfig.getPage().navigate(Constants.URL_LOGIN);
    }

    @When("Type email {string}")
    public void typeEmail(String value){
        Pages.loginPage().typeEmail(value);
    }
}
