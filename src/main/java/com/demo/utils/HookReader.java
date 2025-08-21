package com.demo.utils;

import com.demo.actions.APIActions;
import com.demo.actions.Actions;
import com.demo.core.logger.DefaultLogger;
import com.demo.data.HookContext;
import com.demo.data.SiteContext;
import com.demo.data.User;
import org.apache.commons.lang3.function.TriFunction;
import org.testng.Assert;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HookReader extends DefaultLogger {

    private final Pattern REGISTER_PATTERN = Pattern.compile("^@RegisterCreatedUser(\\d+)");
    private final Pattern CHECK_API_PATTERN = Pattern.compile("^@CheckAPIBeforeTest");
    private final Pattern DELETE_CREATED_USER_PATTERN = Pattern.compile("^@DeleteCreatedUser(\\d+)");
    private final Pattern DELETE_USER_PATTERN = Pattern.compile("^@DeleteUser(\\d+)WithPlus");
    private final Pattern SEGMENT_PATTERN = Pattern.compile("For(\\d+)Segment");
    private final Pattern BRAND_PATTERN = Pattern.compile("For(\\d+)Brand");

    private Pattern chosenPattern;

    private final List<Pattern> BEFORE_PATTERNS = Arrays.asList(
    );

    private final List<Pattern> AFTER_PATTERNS = Arrays.asList(
    );

    public void doBeforeActions(Collection<String> hookNames) {
        hookNames.forEach(hookName -> {
            if (!isHookFromPatterns(hookName, BEFORE_PATTERNS)) return;
            HookContext hookContext = parseHookContext(hookName);

            //Here should be assertions with Before_patterns to do actions

            logHookInfo(hookName, hookContext);
        });
    }

    public void doAfterActions(Collection<String> hookNames) {
        hookNames.forEach(hookName -> {
            if (!isHookFromPatterns(hookName, AFTER_PATTERNS)) return;
            HookContext hookContext = parseHookContext(hookName);

            //Here should be assertions with After_patterns to do actions

            logHookInfo(hookName, hookContext);
        });
    }

    private Optional<Integer> extractNumber(String input, Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            if (matcher.groupCount() >= 1) {
                try {
                    return Optional.of(Integer.parseInt(matcher.group(1)));
                } catch (NumberFormatException e) {
                    logError("Failed to parse number from hook name: " + input);
                }
            }
        }
        return Optional.empty();
    }

    private boolean isHookFromPatterns(String hookName, List<Pattern> patterns) {
        return patterns.stream().anyMatch(pattern -> {
            if (pattern.matcher(hookName).find()) {
                chosenPattern = pattern;
                return true;
            }
            return false;
        });
    }

    private HookContext parseHookContext(String hookName) {
        int patternNumber = extractNumber(hookName, chosenPattern).orElse(1);
        int segmentNumber = extractNumber(hookName, SEGMENT_PATTERN).orElse(1);
        int brandNumber = extractNumber(hookName, BRAND_PATTERN).orElse(1);
        return new HookContext(hookName, patternNumber, segmentNumber, brandNumber);
    }

    private void logHookInfo(String hookName, HookContext ctx) {
        logInfo("HOOKNAME=" + hookName
                + ", PatternNumber=" + ctx.getPatternNumber()
                + ", Segment=" + ctx.getSegmentNumber()
                + ", Brand=" + ctx.getBrandNumber());
    }

    // Default method hooks

    public static String insertNumbersToEmail(String email, String insert) {
        int atIndex = email.indexOf('@');
        if (atIndex == -1) {
            throw new IllegalArgumentException("Email doesn't contain '@'");
        }

        String createdEmail = email.substring(0, atIndex) + "+" + insert + email.substring(atIndex);
        logStaticInfo("Created email: " + createdEmail);
        return createdEmail;
    }

    private void registerCreatedUser(
            Function<APIActions, TriFunction<String, String, SiteContext, Boolean>> apiFunctionSelector,
            User user,
            SiteContext siteContext
    ) {
        APIActions api = Actions.apiActions(false);
        TriFunction<String, String, SiteContext, Boolean> function = apiFunctionSelector.apply(api);

        boolean created = function.apply(user.getEmail(), user.getPassword(), siteContext);
        Assert.assertTrue(created, "Can't create user via API");
    }

    private User createDefaultUser(int userIndex) {
        return initializeCreatedUser(
                Constants.globalContext.get().getEmailUser(1), userIndex
        );
    }

    private User initializeCreatedUser(User user, int userIndex) {
        String email = user.getEmail();
        String password = Generator.genString(10) + "!P1";

        User createdUser = new User();
        createdUser.setEmail(email);
        createdUser.setPassword(password);
        createdUser.setMailReader(user.mailReader);

        if (userIndex < 0) {
            Assert.fail("Incorrect index of user: " + userIndex);
        }

        while (Constants.globalContext.get().getCreatedUsers().size() < userIndex)
            Constants.globalContext.get().getCreatedUsers().add(new User());
        Constants.globalContext.get().getCreatedUsers().add(userIndex, createdUser);

        return Constants.globalContext.get().getCreatedUsers().get(userIndex);
    }

    // Check API hook method
    private void checkAPIAvailable(SiteContext siteContext) {
        if (Constants.API_AUTHORIZATION_HEADER.isEmpty()) {
            throw new IllegalArgumentException("API doesn't work on this site, because authorization header is empty! Please, set it in "
                    + System.getProperty("environment", "google") + ".properties file and try again");
        }
    }
}
