package com.demo.pages;

import com.demo.core.base.PageTools;
import com.microsoft.playwright.Page;
import io.cucumber.java.en.When;
import io.qameta.allure.Step;

public class LoginPage extends PageTools {
    public LoginPage(Page page) {
        super(page);
    }

    private final String emailField = "//input[@id='username']";
    private final String passwordField = "//input[@id='password']";
    private final String loginButton = "//button[span[text()='Log in']]";

    @Step("Type email")
    public void typeEmail(String value){
        type(value,emailField);
    }

    @Step("Type password")
    public void typePassword(String value){
        type(value, passwordField);
    }

    @Step("Click Log in button")
    public void clickLogInButton(){
        click(loginButton);
    }
}
