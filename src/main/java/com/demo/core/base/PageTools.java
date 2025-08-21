package com.demo.core.base;

import com.demo.core.logger.DefaultLogger;
import com.demo.core.config.PlaywrightConfig;
import com.demo.data.enums.ElementSelection;
import com.demo.utils.Constants;
import com.demo.utils.LocatorParser;
import com.demo.utils.PlaywrightTools;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.testng.Assert;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.demo.core.config.PlaywrightConfig.getPage;


public class PageTools extends DefaultLogger {

    private static String getPreviousMethodNameAsText(int depth) {
        String methodName = Thread.currentThread().getStackTrace()[depth].getMethodName();
        String replacedMethodName = methodName.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
        return replacedMethodName.substring(0, 1).toUpperCase() + replacedMethodName.substring(1).toLowerCase();
    }

    private Locator byLocator(String selector, ElementSelection selection, Object... args) {
        Locator base = PlaywrightTools.isInFrame()
                ? LocatorParser.parseLocator(PlaywrightTools.getCurrentFrame(), selector, args)
                : LocatorParser.parseLocator(PlaywrightConfig.getPage(), selector, args);

        if (selection == ElementSelection.FIRST) {
            return base.first();
        }

        return base;
    }

    private Locator byLocator(String selector, Object... args) {
        return byLocator(selector, ElementSelection.DEFAULT, args);
    }

    /**
     * Should be
     */
    protected void shouldMatchText(String pattern, String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(2), selector, args, locator -> {
            String actualText = locator.innerText();
            if (!actualText.matches(pattern)) {
                throw new AssertionError("Text does not match pattern.\nExpected regex: " + pattern + "\nActual text: " + actualText);
            }
        });
    }

    protected void shouldNotBeEmpty(String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(2), selector, args, locator -> {
            String actualText = locator.innerText().trim();
            if (actualText.isEmpty()) {
                throw new AssertionError("Element text is empty, but should not be.");
            }
        });
    }

    protected void shouldNotHaveClass(String className, String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(2), selector, args, locator -> {
            if (locator.getAttribute("class") != null && locator.getAttribute("class").contains(className)) {
                throw new AssertionError("Element has class '" + className + "' but should not.");
            }
        });
    }

    protected void shouldHaveClass(String className, String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(2), selector, args, locator -> {
            String classes = locator.getAttribute("class");
            if (classes == null || !classes.contains(className)) {
                throw new AssertionError("Element does not have expected class '" + className + "'. Actual classes: " + classes);
            }
        });
    }

    /**
     * Main Actions
     */
    private void clickInternal(String selector, ElementSelection selection, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(3), selector, selection, args, Locator::click);
    }

    protected void click(String selector, ElementSelection selection, Object... args) {
        clickInternal(selector, selection, args);
    }

    protected void click(String selector, Object... args) {
        clickInternal(selector, ElementSelection.DEFAULT, args);
    }

    protected void doubleClick(String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(2), selector, args, Locator::dblclick);
    }

    protected void jsClick(String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(2), selector, args, locator -> {
            locator.evaluate("el => el.click()");
        });
    }

    @Deprecated
    protected void typeDeprecated(String text, String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(2), selector, args, locator -> {
            locator.type(text);
        });
    }

    protected void type(String text, String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(2), selector, args, locator -> {
            locator.fill(text);
        });
    }

    protected void wipeText(String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(2), selector, args, locator -> {
            locator.fill("");
        });
    }

    protected void uploadFile(String filePath, String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(2), selector, args, locator -> {
            locator.setInputFiles(Paths.get(filePath));
        });
    }

    protected void mouseHover(String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(2), selector, args, Locator::hover);
    }

    protected void clickEnterButton(String selector, Object... args) {
        clickButtonOnKeyboard(selector, "Enter", args);
    }

    protected void clickEscapeButton(String selector, Object... args) {
        clickButtonOnKeyboard(selector, "Escape", args);
    }

    protected void clickTabButton(String selector, Object... args) {
        clickButtonOnKeyboard(selector, "Tab", args);
    }

    protected void clickBackspaceButton(String selector, Object... args) {
        clickButtonOnKeyboard(selector, "Backspace", args);
    }

    protected void clickDeleteButton(String selector, Object... args) {
        clickButtonOnKeyboard(selector, "Delete", args);
    }

    protected void clickArrowLeftButton(String selector, Object... args) {
        clickButtonOnKeyboard(selector, "ArrowLeft", args);
    }

    protected void clickArrowUpButton(String selector, Object... args) {
        clickButtonOnKeyboard(selector, "ArrowUp", args);
    }

    protected void clickArrowRightButton(String selector, Object... args) {
        clickButtonOnKeyboard(selector, "ArrowRight", args);
    }

    protected void clickArrowDownButton(String selector, Object... args) {
        clickButtonOnKeyboard(selector, "ArrowDown", args);
    }

    protected void clickSpaceButton(String selector, Object... args) {
        clickButtonOnKeyboard(selector, "Space", args);
    }

    private void clickButtonOnKeyboard(String selector, String keyButton, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(2), selector, args, locator -> {
            locator.press(keyButton);
        });
    }

    protected void selectOption(String selector, String option, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(2), selector, args, locator -> {
            locator.selectOption(option);
        });
    }

    protected void waitForElementVisibility(String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(2), selector, args, locator -> {
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        });
    }

    protected void waitForElementInvisibility(String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(2), selector, args, locator -> {
            try {
                Locator.WaitForOptions options = new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.HIDDEN)
                        .setTimeout((double) Constants.TIMEOUT_BEFORE_FAIL * 1000);
                locator.waitFor(options);
            } catch (PlaywrightException e) {
                if (e.getMessage().contains("Object doesn't exist")) {
                    logInfo("Element already removed: " + selector);
                } else if (e.getMessage().contains("Timeout")) {
                    Assert.fail("Timeout reached: Element not hidden — " + selector);
                } else {
                    throw e;
                }
            }
        });
    }

    private void waitForElementClickableInternal(String selector, ElementSelection selection, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(3), selector, selection, args,
                locator -> locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE)));

        for (int i = 0; i < 25; i++) {
            Locator locator = byLocator(selector, selection, args);
            if (locator.isVisible() && locator.isEnabled()) {
                return;
            }
            PlaywrightTools.sleep(Constants.NANO_TIMEOUT);
        }
        throw new RuntimeException("Element '" + selector + "' is not clickable after timeout.");
    }

    protected void waitForElementClickable(String selector, ElementSelection selection, Object... args) {
        waitForElementClickableInternal(selector, selection, args);
    }

    protected void waitForElementClickable(String selector, Object... args) {
        waitForElementClickableInternal(selector, ElementSelection.DEFAULT, args);
    }

    protected Frame waitForFrameAndReturn(String iframeId) {
        ElementHandle iframeHandle = getPage().waitForSelector("iframe#" + iframeId);
        Frame frame = iframeHandle.contentFrame();
        frame.waitForSelector("body");
        return frame;
    }

    /**
     * Is condition
     */

    protected boolean isElementVisible(String selector, Object... args) {
        return isElementVisible(selector, true, args);
    }

    protected boolean isElementClickable(String selector, Object... args) {
        return isElementClickable(selector, true, args);
    }

    protected boolean isElementDisabled(String selector, Object... args) {
        return isElementDisabled(selector, true, args);
    }

    protected boolean isElementChecked(String selector, Object... args) {
        return isElementChecked(selector, true, args);
    }

    private boolean isElementVisible(String selector, boolean isShowLogs, Object... args) {
        return checkLocatorState(getPreviousMethodNameAsText(3), isShowLogs, selector, args, Locator::isVisible);
    }

    private boolean isElementClickable(String selector, boolean isShowLogs, Object... args) {
        return checkLocatorState(getPreviousMethodNameAsText(3), isShowLogs, selector, args, Locator::isEnabled);
    }

    private boolean isElementDisabled(String selector, boolean isShowLogs, Object... args) {
        return checkLocatorState(getPreviousMethodNameAsText(3), isShowLogs, selector, args, Locator::isDisabled);
    }

    private boolean isElementChecked(String selector, boolean isShowLogs, Object... args) {
        return checkLocatorState(getPreviousMethodNameAsText(3), isShowLogs, selector, args, Locator::isChecked);
    }

    protected boolean isFirstElementVisibleCheck(String selector, long seconds, Object... args) {
        return isElementVisibleCheckInternal(selector, seconds, ElementSelection.DEFAULT, args);
    }

    protected boolean isElementVisibleCheck(String selector, long seconds, Object... args) {
        return isElementVisibleCheckInternal(selector, seconds, ElementSelection.FIRST, args);
    }

    private boolean isElementVisibleCheckInternal(String selector, long seconds, ElementSelection selection, Object... args) {
        int periodNumber = 4;

        Locator parsedLocator = byLocator(selector, selection, args);
        logInfo(getPreviousMethodNameAsText(2) +
                (selection == ElementSelection.FIRST ? ", first element --> " : ", element --> ") + parsedLocator);

        try {
            for (double i = 0; i < seconds * periodNumber; i++) {
                if (parsedLocator.isVisible()) {
                    logInfo((selection == ElementSelection.FIRST ? "First element" : "Element") +
                            " is visible after " + String.format("%.2f", i / periodNumber) + " seconds");
                    return true;
                }
                PlaywrightTools.sleep((double) Constants.NANO_TIMEOUT / periodNumber);
            }
            logInfo((selection == ElementSelection.FIRST ? "First element" : "Element") +
                    " is not visible after " + seconds + " seconds");
            return false;
        } catch (PlaywrightException e) {
            if (e.getMessage().contains("Object doesn't exist")) {
                logInfo((selection == ElementSelection.FIRST ? "First element" : "Element") +
                        " already removed: " + selector);
                return false;
            } else {
                throw e;
            }
        }
    }

    /**
     * Getters
     */
    protected String getInnerHTMLFromFrame(String frameId, String selector) {
        return getHtml(waitForFrameAndReturn(frameId)
                .locator(selector)
                .innerHTML());
    }

    protected String getElementText(String selector, Object... args) {
        return extractFromLocator(getPreviousMethodNameAsText(2), selector, args, Locator::inputValue);
    }

    protected String getElementInnerHTML(String selector, Object... args) {
        return extractFromLocator(getPreviousMethodNameAsText(2), selector, args, Locator::innerHTML);
    }

    protected String getElementInnerText(String selector, Object... args) {
        return extractFromLocator(getPreviousMethodNameAsText(2), selector, args, Locator::innerText);
    }

    protected String getElementTextContent(String selector, Object... args) {
        return extractFromLocator(getPreviousMethodNameAsText(2), selector, args, Locator::textContent);
    }

    protected String getElementAttributeValue(String attr, String selector, Object... args) {
        return extractFromLocator(getPreviousMethodNameAsText(2), selector, args, locator -> locator.getAttribute(attr));
    }

    protected String getHiddenElementAttributeValue(String attr, String selector, Object... args) {
        return extractFromLocator(getPreviousMethodNameAsText(2), selector, args, locator -> {
            if (!locator.isHidden()) {
                throw new AssertionError("Element is not hidden");
            }
            return locator.getAttribute(attr);
        });
    }

    protected String getDisabledElementAttributeValue(String attr, String selector, Object... args) {
        return extractFromLocator(getPreviousMethodNameAsText(2), selector, args, locator -> {
            if (!locator.isDisabled()) {
                throw new AssertionError("Element is not disabled");
            }
            return locator.getAttribute(attr);
        });
    }

    protected List<String> getElementsText(String selector, Object... args) {
        Locator locator = byLocator(selector, args);
        logInfo(getPreviousMethodNameAsText(2) + ", elements --> " + locator);
        return locator.allInnerTexts();
    }

    protected List<String> getElementsTextWithWait(int waitTimeout, String selector, Object... args) {
        Locator locator = byLocator(selector, args);
        logInfo(getPreviousMethodNameAsText(2) + ", elements --> " + locator);
        PlaywrightTools.sleep(waitTimeout);
        return locator.allInnerTexts();
    }

    protected void scrollToElement(String selector, Object... args) {
        Locator locator = byLocator(selector, args);
        logInfo(getPreviousMethodNameAsText(2) + ", element --> " + locator);
        locator.scrollIntoViewIfNeeded();
    }

    protected void scrollToPlaceElementInCenter(String selector, Object... args) {
        Locator locator = byLocator(selector, args);
        logInfo(getPreviousMethodNameAsText(2) + ", element --> " + locator);
        PlaywrightConfig.getPage().evaluate("el => el.scrollIntoView({block: 'center'})", locator);
    }

    protected ElementHandle getWebElement(String selector, Object... args) {
        Locator locator = byLocator(selector, args);
        return locator.elementHandle();
    }

    /**
     * Work with colors
     */
    protected Path downloadFile(String selector, Object... args) {
        try {
            Locator locator = byLocator(selector, args);
            Download download = PlaywrightConfig.getPage().waitForDownload(() -> locator.click());
            return download.path();
        } catch (Exception e) {
            logError("Failed to download file using selector '{}'", e, selector);
            return null;
        }
    }

    /**
     * Private methods
     */

    private boolean checkLocatorState(String methodName, boolean isShowLogs, String selector, Object[] args, Function<Locator, Boolean> stateCheck) {
        Locator parsedLocator = byLocator(selector, args);
        if (isShowLogs) logInfo(methodName + ", element --> " + parsedLocator);
        try {
            return stateCheck.apply(parsedLocator);
        } catch (PlaywrightException e) {
            if (e.getMessage().contains("Object doesn't exist")) {
                logInfo("Element already removed: " + selector);
                return false;
            } else {
                throw e;
            }
        }
    }

    private void performOnLocator(String methodName, String selector, ElementSelection selection, Object[] args, Consumer<Locator> action) {
        Locator parsedLocator = byLocator(selector, selection, args);
        logInfo(methodName + ", element --> " + parsedLocator);
        action.accept(parsedLocator);
    }

    private void performOnLocator(String methodName, String selector, Object[] args, Consumer<Locator> action) {
        performOnLocator(methodName, selector, ElementSelection.DEFAULT, args, action);
    }

    private <T> T extractFromLocator(String methodName, String selector, Object[] args, Function<Locator, T> extractor) {
        Locator parsedLocator = byLocator(selector, args);
        logInfo(methodName + ", element --> " + parsedLocator);
        return extractor.apply(parsedLocator);
    }

    private String getHtml(String html) {
        return html.replaceAll("amp;", "").replace("\"", "").trim();
    }
}