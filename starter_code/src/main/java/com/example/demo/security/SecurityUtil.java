package com.example.demo.security;

public class SecurityUtil {

    public static final String secretKey="4_s3cr3t_k3y";
    public static final String loginUser="/api/user/login";
    public static final String createUser="/api/user/create";
    public static final String TOKEN_BEARER = "Bearer ";
    public static final String INVALID_CREDENTIALS = "Wrong username and password";
    public static final int SESSION_EXPIRATION_TIME = 10*60*60;
    public static final String HEADER_AUTH_KEY= "Authorization";



}
