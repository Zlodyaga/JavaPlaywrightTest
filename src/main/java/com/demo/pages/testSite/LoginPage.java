package com.demo.pages.testSite;

import com.demo.core.base.PageTools;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

public class LoginPage extends PageTools {
    public LoginPage(Page page) {
        super(page);
    }

    private final String inputFieldById = "//input[normalize-space(@id)=\"%s\"]";
    private final String loginButton = "//button[span[normalize-space(text())=\"%s\"]]";

    @Step("Type email")
    public void typeEmail(String value){
        typeIntoFieldName(value, "username");
    }

    @Step("Type password")
    public void typePassword(String value){
        typeIntoFieldName(value, "password");
    }

    @Step("Click Log in button")
    public void clickLogInButton(){
        click(loginButton, "Log in");
    }

    private void typeIntoFieldName(String value, String fieldName) {
        type(value, inputFieldById, fieldName);
    }
}
