package com.demo.bdd.ithillel;

import com.demo.core.allure.AllureLogger;
import com.demo.pages.Pages;
import com.demo.utils.Constants;
import com.demo.utils.PlaywrightTools;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

public class HomeStepDefinitions extends AllureLogger {

    @Given("Open Ithillel home page")
    public void openIthillelUrl(){
        PlaywrightTools.openUrl(Constants.ITHILLEL_URL);
    }

    @When("Click course {string}")
    public void clickCourse(String value){
        Pages.ithillel().homePage().clickCourse("Тестування");

    }

}
