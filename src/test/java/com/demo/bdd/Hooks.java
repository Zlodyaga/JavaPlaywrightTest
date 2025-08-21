package com.demo.bdd;

import com.demo.core.logger.DefaultLogger;
import com.demo.core.allure.AllureTools;
import com.demo.core.config.PlaywrightConfig;
import com.demo.data.GlobalContext;
import com.demo.data.SiteContext;
import com.demo.data.User;
import com.demo.utils.*;
import io.cucumber.java.*;
import io.qameta.allure.Allure;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Hooks extends DefaultLogger {

    protected static PropertyLoader propertyStaticLoader;
    protected PropertyLoader propertyLoader;
    protected HookReader hookReader = new HookReader();
    protected ThreadLocal<Scenario> scenario = new ThreadLocal<>();
    private static HashMap<String, String> passwordsForUsers = new HashMap<>();

    @BeforeAll
    public static void beforeAll() {
        loadSettingsFromFile();
        loadConstantsFromFile(System.getProperty("environment", "google"));
    }

    @Before(order = 1)
    public void beforeScenario(Scenario scenario) {
        this.scenario.set(scenario);
        String scenarioName = extractCodePrefix(scenario.getName());
        Thread.currentThread().setName(scenarioName);
        PlaywrightConfig.setScenarioName(scenarioName);

        Constants.globalContext.set(new GlobalContext());
        loadUsersFromFile();

        PlaywrightConfig.createBrowserConfig();

        configLog(scenarioName);

        PlaywrightTools.openUrl(Constants.GOOGLE_HOMEPAGE_URL);

        renameScenarioAllureWithArg(scenarioName);
        hookReader.doBeforeActions(this.scenario.get().getSourceTagNames());
    }

    @After
    public synchronized void tearDown() {
        if (scenario.get().isFailed()) {
            byte[] screenshot = AllureTools.attachScreenshot(PlaywrightConfig.getPage());
            if (screenshot != null)
                scenario.get().attach(screenshot, "image/png", "Failure Screenshot");
            try {
                scenario.get().attach(AllureTools.attachLogFile(), "text/plain", "Failure Log File");
            } catch (Throwable e) {
                logError("Failed to attach log file: " + e.getMessage());
            }
            try {
                scenario.get().attach(findAndReadVideo(extractCodePrefix(scenario.get().getName())), "video/webm", "Failure Video");
            } catch (Throwable e) {
                logError("Failed to attach video file: " + e.getMessage());
            }
        }
        hookReader.doAfterActions(this.scenario.get().getSourceTagNames());

        logInfo("Closing Playwright browser...");
        PlaywrightConfig.closeBrowser();
        logInfo("Playwright browser closed!");
        Constants.globalContext.remove();
        scenario.remove();
    }

    private static void loadSettingsFromFile() {
        propertyStaticLoader = new PropertyLoader("settings.properties");
        Constants.NANO_TIMEOUT = Integer.parseInt(propertyStaticLoader.get("NANO_TIMEOUT"));
        Constants.MICRO_TIMEOUT = Integer.parseInt(propertyStaticLoader.get("MICRO_TIMEOUT"));
        Constants.MINI_TIMEOUT = Integer.parseInt(propertyStaticLoader.get("MINI_TIMEOUT"));
        Constants.SMALL_TIMEOUT = Integer.parseInt(propertyStaticLoader.get("SMALL_TIMEOUT"));
        Constants.BIG_TIMEOUT = Integer.parseInt(propertyStaticLoader.get("BIG_TIMEOUT"));

        Constants.SCREEN_WIDTH = Integer.parseInt(propertyStaticLoader.get("SCREEN_WIDTH"));
        Constants.SCREEN_HEIGHT = Integer.parseInt(propertyStaticLoader.get("SCREEN_HEIGHT"));

        Constants.MAILINATOR_URL = propertyStaticLoader.get("MAILINATOR_URL");

        Constants.TIMEOUT_BEFORE_FAIL = Integer.parseInt(propertyStaticLoader.get("TIMEOUT_BEFORE_FAIL"));
    }

    private void loadUsersFromFile() {
        propertyLoader = new PropertyLoader("users.properties");
        ArrayList<User> users = new ArrayList<>();

        Set<String> keys = propertyLoader.getKeys();
        Set<Integer> userIndices = new TreeSet<>();

        for (String key : keys) {
            if (key.matches("user\\d+\\.mail")) {
                String index = key.replaceAll("user(\\d+)\\.mail", "$1");
                userIndices.add(Integer.parseInt(index));
            }
        }

        for (int i : userIndices) {
            String mailRaw = propertyLoader.get("user" + i + ".mail");
            String email = insertNumbersToEmail(mailRaw, Generator.getRandomStringNumber(6));
            String emailPassword = propertyLoader.get("user" + i + ".password");
            String appPassword = propertyLoader.get("user" + i + ".app.password");

            if (!email.isEmpty()
                    && emailPassword != null && !emailPassword.isEmpty()
                    && appPassword != null && !appPassword.isEmpty()) {

                User user;
                if (System.getProperty("mailinatorOff", "true").equals("true"))
                    user = new User(email, emailPassword, appPassword, true);
                else
                    user = new User(email.replace("@gmail.com", "@mailinator.com"), emailPassword, appPassword, false);

                if (passwordsForUsers.containsKey("user" + i)) {
                    user.setPasswordDecoded(passwordsForUsers.get("user" + i));
                } else {
                    logInfo("Password for user " + i + " is not set in properties file " + System.getProperty("environment", "google") + ".properties");
                }

                users.add(user);
            } else {
                logInfo("User " + i + " is not loaded. Check user" + i + ".properties file");
            }
        }
        Constants.globalContext.get().setEmailUsers(users);
    }

    private static void loadConstantsFromFile(String environment) {
        int index = environment.indexOf('-');
        if (index == -1)
            propertyStaticLoader.reloadProperties(environment + ".properties");
        else
            propertyStaticLoader.reloadProperties(environment.substring(0, index) + "/" + environment + ".properties");


        String segmentCountStr = propertyStaticLoader.get("segment.count");
        int segmentCount = segmentCountStr != null ? Integer.parseInt(segmentCountStr) : 1;
        String baseUrl;

        for (int segmentNumber = 1; segmentNumber <= segmentCount; segmentNumber++) {
            for (int brandNumber = 1; propertyStaticLoader.get("base-" + segmentNumber + "-" + brandNumber + ".url") != null; brandNumber++) {
                baseUrl = propertyStaticLoader.get("base-" + segmentNumber + "-" + brandNumber + ".url");
                Constants.BASE_URL_MAP.put(new SiteContext(segmentNumber, brandNumber), baseUrl);
            }
        }

        Constants.SITE_NAME = propertyStaticLoader.get("site.name").toLowerCase();
        try {
            Constants.API_AUTHORIZATION_HEADER = new String(Constants.DECODER.decode(propertyStaticLoader.get("api.authorization.header")), StandardCharsets.UTF_8);
        } catch (Exception e) {
            logStaticError("API KEY IS EMPTY IN " + environment + ".properties");
            Constants.API_AUTHORIZATION_HEADER = "";
        }

        String passwordForUser;

        for (int i = 1; propertyStaticLoader.get("user" + i + ".password") != null; i++) {
            passwordForUser = propertyStaticLoader.get("user" + i + ".password");
            passwordsForUsers.put("user" + i, passwordForUser);
        }
    }

    public static String insertNumbersToEmail(String email, String insert) {
        int atIndex = email.indexOf('@');
        if (atIndex == -1) {
            throw new IllegalArgumentException("Email doesn't contain '@'");
        }

        String createdEmail = email.substring(0, atIndex) + "+" + insert + email.substring(atIndex);
        logStaticInfo("Created email: " + createdEmail);
        return createdEmail;
    }

    private String extractCodePrefix(String input) {
        if (input == null || input.isEmpty()) return "";

        Pattern pattern = Pattern.compile("^([A-Z]+\\d+)");
        Matcher matcher = pattern.matcher(input.trim());

        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private byte[] findAndReadVideo(String scenarioName) {
        Path videoDir = Paths.get("target/videos", scenarioName);
        if (!Files.exists(videoDir)) return null;

        try (Stream<Path> files = Files.list(videoDir)) {
            Optional<Path> webmFile = files
                    .filter(path -> path.toString().endsWith(".webm"))
                    .findFirst();

            if (webmFile.isPresent()) {
                try {
                    return Files.readAllBytes(webmFile.get());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void renameScenarioAllureWithArg(String scenarioName) {
        Allure.getLifecycle().updateTestCase(testResult -> {
            testResult.setName(scenarioName);
            testResult.setFullName("Scenario: " + scenarioName);
        });
    }
}
