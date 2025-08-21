package com.demo.utils;

import com.demo.core.config.PlaywrightConfig;
import com.demo.core.logger.DefaultLogger;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PlaywrightTools extends DefaultLogger {
    private static Frame currentFrame = null;

    private static Page getPage() {
        return PlaywrightConfig.getPage();
    }

    public static Frame getCurrentFrame() {
        if (currentFrame == null) {
            throw new IllegalStateException("No active frame context. Did you forget to call switchToFrame()?");
        }
        return currentFrame;
    }

    public static boolean isInFrame() {
        return currentFrame != null;
    }

    public static List<Page> getTabsList() {
        return getPage().context().pages();
    }

    public static int getTabsCount() {
        return getTabsList().size();
    }

    public static void sleep(int sec) {
        try {
            Thread.sleep(sec * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void sleep(double numberDouble) {
        try {
            Thread.sleep((int) (numberDouble * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Step("Closing current tab...")
    public static void closeCurrentTab() {
        logStaticInfo("Closing current tab...");
        getPage().close();
    }

    @Step("Opening url - '{url}'")
    public static void openUrl(String url) {
        logStaticInfo("Opening url '{}'", url);
        synchronized (PlaywrightTools.class) {
            try {
                getPage().navigate(url);
            }
            catch (PlaywrightException e) {
                e.getMessage().contains("ERR_ABORTED at");
                if (e.getMessage().contains("ERR_ABORTED at")) {
                    logStaticInfo("Loading URL, caused error \"ERR_ABORTED\", wait for " + Constants.MICRO_TIMEOUT + " seconds before navigating to the next page...");
                    sleep(Constants.MICRO_TIMEOUT);
                    getPage().navigate(url);
                    return;
                }
                throw e;
            }
        }
    }

    @Step("Refreshing...")
    public static void refresh() {
        logStaticInfo("Refreshing...");
        getPage().reload();
    }

    @Step("Clearing cookies...")
    public static void clearCookies() {
        logStaticInfo("Clearing cookies...");
        getPage().context().clearCookies();
    }

    @Step("Switching to frame by name...")
    public static void switchToFrame(String frameName) {
        logStaticInfo("Switching to frame '{}'", frameName);
        Frame frame = getPage().frame(frameName);
        if (frame == null) {
            throw new RuntimeException("Frame with name '" + frameName + "' not found");
        }
        logStaticInfo("Frame is set - " + frame);
        currentFrame = frame;
    }

    @Step("Switching to default content (main page)")
    public static void switchToDefaultContent() {
        logStaticInfo("Switching to default content...");
        currentFrame = null;
    }

    @Step("Switching to first tab...")
    public static void switchToFirstTab() {
        logStaticInfo("Switching to first tab...");
        List<Page> tabs = getTabsList();
        if (tabs.size() != 1) {
            Page lastPage = tabs.get(0);
            PlaywrightConfig.setPage(lastPage);
        }
    }

    @Step("Switching to last tab...")
    public static void switchToLastTab() {
        logStaticInfo("Switching to last tab...");
        List<Page> tabs = getTabsList();
        Page lastPage = tabs.get(tabs.size() - 1);
        PlaywrightConfig.setPage(lastPage);
    }

    @Step("Open url in new window")
    public static void openUrlInNewWindow(String url) {
        logStaticInfo("Open url in new window");
        Page newPage = getPage().context().newPage();
        newPage.navigate(url);
        PlaywrightConfig.setPage(newPage);
    }

    public static void closeAllTabsExceptCurrent() {
        Page current = getPage();
        for (Page tab : getTabsList()) {
            if (!tab.equals(current)) {
                tab.close();
            }
        }
    }

    @Step("Get current url")
    public static String getCurrentUrl() {
        Page page = getPage();
        try {
            return page.evaluate("() => window.location.href").toString();
        } catch (PlaywrightException e) {
            if (e.getMessage().contains("most likely because of a navigation")) {
                logStaticInfo("LOADING URL");
                return "";
            }
        }
        return "";
    }

    public static void setViewportSize(int width, int height) {
        getPage().setViewportSize(width, height);
    }
}
