package com.monterdev.constants;

public class ErrorConstants {

    private static final String APP_ERROR_MESSAGE = "APP_ERROR_MESSAGE";
    private static final String APP_ERROR_CODE = "APP_ERROR_CODE";
    private static final String API_ERROR_CODE = "API_ERROR_CODE";
    private static final String API_ERROR_MESSAGE = "API_ERROR_MESSAGE";

    public static String APPLICATION_ERROR_MESSAGE() {
        return GlobalConfiguration.getConfigValue(APP_ERROR_MESSAGE);
    }

    public static String APPLICATION_ERROR_CODE() {
        return GlobalConfiguration.getConfigValue(APP_ERROR_CODE);
    }

    public static String API_ERROR_CODE() {
        return GlobalConfiguration.getConfigValue(API_ERROR_CODE);
    }

    public static String API_ERROR_MESSAGE() {
        return GlobalConfiguration.getConfigValue(API_ERROR_MESSAGE);
    }
}
