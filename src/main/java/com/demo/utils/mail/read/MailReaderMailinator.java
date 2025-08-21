package com.demo.utils.mail.read;

import com.demo.data.abstracts.AbstractMailReader;
import com.demo.pages.Pages;
import com.demo.utils.Constants;
import com.demo.utils.PlaywrightTools;
import org.testng.Assert;

public class MailReaderMailinator extends AbstractMailReader {

    public MailReaderMailinator(String email) {
        super(email);
    }

    @Override
    public String getTextFromEmail(String recipient, String subject, int timeoutSec) {
        operMailinatorInNewPage();

        Pages.mailinatorPage().typeEmailIntoInputFieldEmail(recipient);
        PlaywrightTools.sleep(Constants.NANO_TIMEOUT);
        Pages.mailinatorPage().clickGoButton();

        if (!Pages.mailinatorPage().isMessageWithSearchedSubjectInBoxEmailVisible(subject, timeoutSec))
            Assert.fail("No email found with subject: " + subject);
        Pages.mailinatorPage().clickOnMessageWithSearchedSubjectInBoxEmail(subject);

        Pages.mailinatorPage().switchToIframeMessage();
        String messageText = Pages.mailinatorPage().getMessageBodyText();
        logInfo(messageText);
        PlaywrightTools.switchToDefaultContent();
        return messageText;
    }

    //private methods

    private void operMailinatorInNewPage() {
        PlaywrightTools.openUrlInNewWindow(Constants.MAILINATOR_URL);
    }

    private void closeMailinatorTab() {
        PlaywrightTools.closeCurrentTab();
    }
}
