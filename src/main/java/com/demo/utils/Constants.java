package com.demo.utils;

import com.demo.data.GlobalContext;
import com.demo.data.SiteContext;

import java.util.Base64;
import java.util.HashMap;

public class Constants {
    public static ThreadLocal<GlobalContext> globalContext = new ThreadLocal<>();

    public static String SITE_NAME;
    public static String GOOGLE_HOMEPAGE_URL = "https://www.google.com/";
    //URL constants for site
    public static HashMap<SiteContext, String> BASE_URL_MAP = new HashMap<>();

    //URL constants
    public static String MAILINATOR_URL;

    // Time constants
    public static int NANO_TIMEOUT;
    public static int MICRO_TIMEOUT;
    public static int MINI_TIMEOUT;
    public static int SMALL_TIMEOUT;
    public static int BIG_TIMEOUT;

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    public static int TIMEOUT_BEFORE_FAIL;
    public static String API_AUTHORIZATION_HEADER;
    public static final Base64.Decoder DECODER = Base64.getDecoder();
}
