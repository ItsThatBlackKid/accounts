package com.saokanneh.auth.security;

import com.saokanneh.auth.SpringApplicationContext;

public class SecurityConstants {
    public static final long EXPIRATION_TIME = 1000L * 60L * 60L * 24L * 10L;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users";
    public static String getTokenString() {
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("AppProperties");
        return appProperties.getTokenSecret();
    }
}
