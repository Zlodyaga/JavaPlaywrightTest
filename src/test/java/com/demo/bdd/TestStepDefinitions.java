package com.demo.bdd;

import com.demo.core.logger.DefaultLogger;
import com.demo.utils.Constants;
import com.demo.utils.PlaywrightTools;
import io.cucumber.java.en.When;

public class TestStepDefinitions extends DefaultLogger {

    @When("Open login page")
    public void openLoginPage() {
        PlaywrightTools.openUrl(Constants.LOGIN_URL);
    }

    @When("Test step")
    public void readMails() {
        String email = Constants.globalContext.get().getEmailUser(1).getEmail();
        String password = Constants.globalContext.get().getEmailUser(1).getPassword();
        String appPassword = Constants.globalContext.get().getEmailUser(1).getAppPassword();
        logInfo("User 1 email: " + email + ", password: " + password + ", app password: " + appPassword);
    }
}
