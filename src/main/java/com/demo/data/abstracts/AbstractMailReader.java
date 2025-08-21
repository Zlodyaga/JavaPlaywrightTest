package com.demo.data.abstracts;

import com.demo.core.logger.DefaultLogger;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractMailReader extends DefaultLogger {
    protected String email;
    protected String password;

    protected AbstractMailReader() {
    }

    protected AbstractMailReader(String email) {
        this.email = email;
    }

    protected AbstractMailReader(String email, String password) {
        this(email);
        this.password = password;
    }

    //public abstract methods

    public abstract String getTextFromEmail(String recipient, String subject, int timeoutSec);

    //public methods

    public String getOTP(String message) {
        String OTP = " ";
        Pattern linkPattern = Pattern.compile("[\\d]{6}"); // here you need to define regex as per you need
        logInfo(message);

        Matcher pageMatcher =
                linkPattern.matcher(message);
        while (pageMatcher.find()) {
            OTP = pageMatcher.group(0);
        }
        logInfo(OTP);
        return OTP.substring(OTP.length() - 6);
    }

    public String getOTPFirstLine(String message) {
        String OTP = " ";
        Pattern linkPattern = Pattern.compile("code is:<strong>? [\\d]{6}"); // here you need to define regex as per you need
        Matcher pageMatcher =
                linkPattern.matcher(message);

        while (pageMatcher.find()) {
            OTP = pageMatcher.group(0);
        }
        return OTP.replaceAll("[^0-9]", "");
    }

    public ArrayList<String> getLinksFromMessage(String message) {
        ArrayList<String> links = new ArrayList<>();

        Pattern linkPattern = Pattern.compile
                ("\\b((?:https?|ftp)://[a-zA-Z0-9\\-._~:/?#\\[\\]@!$&'()*+,;=%]+)");
        Matcher pageMatcher = linkPattern.matcher(message);

        while (pageMatcher.find()) {
            links.add(pageMatcher.group(1));
        }
        return links;
    }

    public String getLinkFromMessage(String message, String keyWord) {
        String link = "";
        int linksSize = getLinksFromMessage(message).size();
        for (int i = 0; i < linksSize; i++) {
            link = getLinksFromMessage(message).get(i).replaceAll("\">here</a>.|amp;|\"", "");
            if (link.contains(keyWord))
                break;
        }

        return link;
    }
}
