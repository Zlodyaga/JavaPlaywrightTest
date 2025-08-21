package com.demo.pages.tools;

import com.demo.core.base.PageTools;
import com.demo.data.enums.ElementSelection;
import com.demo.utils.PlaywrightTools;
import io.qameta.allure.Step;


public class MailinatorPage extends PageTools {

    //Start locators
    protected String inputFieldEmail = "//input[@id='inbox_field']";
    protected String buttonGoEmail = "//input[@id='inbox_field']/../button";
    //After send email locators
    protected String messageBoxEmail = "//tr[@ng-repeat='email in emails']";
    protected String messageBoxEmailFrom = messageBoxEmail + "/td[2]";
    protected String messageBoxEmailSubject = messageBoxEmail + "/td[3]";
    protected String messageBoxEmailSubjectWithValue = messageBoxEmailSubject + "[normalize-space(text())=\"%s\"]";
    protected String bodySelector = "body";
    //After open email locators
    protected String iframeEmailMessageHTML = "//iframe[@id='%s']";
    //ids
    protected String iframeEmailMessageHTMLId = "html_msg_body";

    @Step("Type into input field email")
    public void typeEmailIntoInputFieldEmail(String value) {
        type(value, inputFieldEmail);
    }

    @Step("Click Go button")
    public void clickGoButton() {
        waitForElementClickable(buttonGoEmail);
        click(buttonGoEmail);
    }

    @Step("Is message in box email with searched subject is visible")
    public boolean isMessageWithSearchedSubjectInBoxEmailVisible(String value, int timeout) {
        return isFirstElementVisibleCheck(messageBoxEmailSubjectWithValue, timeout, value);
    }

    @Step("Click on message in box email with searched subject")
    public void clickOnMessageWithSearchedSubjectInBoxEmail(String value) {
        waitForElementClickable(messageBoxEmailSubjectWithValue, ElementSelection.FIRST, value);
        click(messageBoxEmailSubjectWithValue, ElementSelection.FIRST, value);
    }

    @Step("Get text from body in email")
    public String getMessageBodyText() {
        return getInnerHTMLFromFrame(iframeEmailMessageHTMLId, bodySelector);
    }

    @Step("Switch to iframe of email message HTML")
    public void switchToIframeMessage() {
        waitForElementVisibility(iframeEmailMessageHTML, iframeEmailMessageHTMLId);
        PlaywrightTools.switchToFrame(iframeEmailMessageHTMLId);
    }
}
