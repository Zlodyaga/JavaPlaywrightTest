package com.demo.core.base;

import com.demo.core.allure.AllureLogger;
import com.demo.utils.LocatorParser;
import com.demo.utils.PlaywrightTools;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;


public class PageTools extends AllureLogger {

    private final Page page;

    public PageTools(Page page) {
        this.page = page;
    }

    private static String getPreviousMethodNameAsText() {
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
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

    private Locator byLocator(String by, Object... args) {
        return LocatorParser.parseLocator(page, by, args);
    }

    /**
     * Should be
     */
    protected void shouldMatchText(String pattern, String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(), selector, args, locator -> {
            String actualText = locator.innerText();
            if (!actualText.matches(pattern)) {
                throw new AssertionError("Text does not match pattern.\nExpected regex: " + pattern + "\nActual text: " + actualText);
            }
        });
    }

    protected void shouldNotBeEmpty(String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(), selector, args, locator -> {
            String actualText = locator.innerText().trim();
            if (actualText.isEmpty()) {
                throw new AssertionError("Element text is empty, but should not be.");
            }
        });
    }

    protected void shouldNotHaveClass(String className, String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(), selector, args, locator -> {
            if (locator.getAttribute("class") != null && locator.getAttribute("class").contains(className)) {
                throw new AssertionError("Element has class '" + className + "' but should not.");
            }
        });
    }

    protected void shouldHaveClass(String className, String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(), selector, args, locator -> {
            String classes = locator.getAttribute("class");
            if (classes == null || !classes.contains(className)) {
                throw new AssertionError("Element does not have expected class '" + className + "'. Actual classes: " + classes);
            }
        });
    }

    /**
     * Main Actions
     */
    protected void click(String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(), selector, args, Locator::click);
    }

    protected void jsClick(String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(), selector, args, locator -> {
            locator.evaluate("el => el.click()");
        });
    }

    protected void type(String text, String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(), selector, args, locator -> {
            locator.type(text);
        });
    }

    protected void typeFill(String text, String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(), selector, args, locator -> {
            locator.fill(text);
        });
    }

    protected void wipeText(String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(), selector, args, locator -> {
            locator.fill("");
        });
    }

    protected void uploadFile(String filePath, String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(), selector, args, locator -> {
            locator.setInputFiles(Paths.get(filePath));
        });
    }

    protected void mouseHover(String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(), selector, args, Locator::hover);
    }

    protected void clickEnterButton(String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(), selector, args, locator -> {
            locator.press("Enter");
        });
    }

    protected void waitForElementVisibility(String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(), selector, args, locator -> {
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        });
    }

    protected void waitForElementInvisibility(String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(), selector, args, locator -> {
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
        });
    }

    protected void waitForElementClickable(String selector, Object... args) {
        performOnLocator(getPreviousMethodNameAsText(), selector, args, locator -> {
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        });
        for(int i = 0; i < 25; i++) {
            if(isElementClickable(selector, args)) {
                return;
            }
            PlaywrightTools.sleep(1);
        }
    }

    /**
     * Is condition
     */
    protected boolean isElementVisible(String selector, Object... args) {
        return checkLocatorState(getPreviousMethodNameAsText(), selector, Locator::isVisible, args);
    }

    protected boolean isElementClickable(String selector, Object... args) {
        return checkLocatorState(getPreviousMethodNameAsText(), selector, Locator::isEnabled, args);
    }

    protected boolean isElementDisabled(String selector, Object... args) {
        return checkLocatorState(getPreviousMethodNameAsText(), selector, Locator::isDisabled, args);
    }

    protected boolean isElementChecked(String selector, Object... args) {
        return checkLocatorState(getPreviousMethodNameAsText(), selector, Locator::isChecked, args);
    }

    /**
     * Getters
     */
    protected String getElementText(String selector, Object... args) {
        return extractFromLocator(getPreviousMethodNameAsText(), selector, args, Locator::innerText);
    }

    protected String getElementAttributeValue(String attr, String selector, Object... args) {
        return extractFromLocator(getPreviousMethodNameAsText(), selector, args, locator -> locator.getAttribute(attr));
    }

    protected String getHiddenElementAttributeValue(String attr, String selector, Object... args) {
        return extractFromLocator(getPreviousMethodNameAsText(), selector, args, locator -> {
            if (!locator.isHidden()) {
                throw new AssertionError("Element is not hidden");
            }
            return locator.getAttribute(attr);
        });
    }

    protected String getDisabledElementAttributeValue(String attr, String selector, Object... args) {
        return extractFromLocator(getPreviousMethodNameAsText(), selector, args, locator -> {
            if (!locator.isDisabled()) {
                throw new AssertionError("Element is not disabled");
            }
            return locator.getAttribute(attr);
        });
    }

    protected List<String> getElementsText(String selector, Object... args) {
        Locator locator = byLocator(selector, args);
        logInfo(getPreviousMethodNameAsText() + ", elements --> " + locator);
        return locator.allInnerTexts();
    }

    protected List<String> getElementsTextWithWait(int waitTimeout, String selector, Object... args) {
        Locator locator = byLocator(selector, args);
        logInfo(getPreviousMethodNameAsText() + ", elements --> " + locator);
        PlaywrightTools.sleep(waitTimeout);
        return locator.allInnerTexts();
    }

    protected void scrollToElement(String selector, Object... args) {
        Locator locator = byLocator(selector, args);
        logInfo(getPreviousMethodNameAsText() + ", element --> " + locator);
        locator.scrollIntoViewIfNeeded();
    }

    protected void scrollToPlaceElementInCenter(String selector, Object... args) {
        Locator locator = byLocator(selector, args);
        logInfo(getPreviousMethodNameAsText() + ", element --> " + locator);
        page.evaluate("el => el.scrollIntoView({block: 'center'})", locator);
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
            Download download = page.waitForDownload(() -> locator.click());
            return download.path();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
    Private methods
     */

    private boolean checkLocatorState(String methodName, String selector, Function<Locator, Boolean> stateCheck, Object... args) {
        Locator parsedLocator = byLocator(selector, args);
        logInfo(methodName + ", element --> " + parsedLocator);
        return stateCheck.apply(parsedLocator);
    }

    private void performOnLocator(String methodName, String selector, Object[] args, Consumer<Locator> action) {
        Locator parsedLocator = byLocator(selector, args);
        logInfo(methodName + ", element --> " + parsedLocator);
        action.accept(parsedLocator);
    }

    private <T> T extractFromLocator(String methodName, String selector, Object[] args, Function<Locator, T> extractor) {
        Locator parsedLocator = byLocator(selector, args);
        logInfo(methodName + ", element --> " + parsedLocator);
        return extractor.apply(parsedLocator);
    }
}