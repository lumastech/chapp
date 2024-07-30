package com.lumastech.chapp.Models;


import android.util.ArrayMap;

import java.util.Arrays;

public class RegisterResponse {
    String message, token;
    boolean success = false;
    Arrays errors;
    User user;
    Object rdata;

    public RegisterResponse(String message, String token, boolean success, User user, Object rdata) {
        this.message = message;
        this.token = token;
        this.success = success;
        this.user = user;
        this.rdata = rdata;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
