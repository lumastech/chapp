package com.lumastech.chapp.Models;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ApiResponse {
    String message = "", token= "null";
    boolean success = false;
    Sos sos;
    User user;
    Center center;
    ArrayList<Center> centers;
    Array errors;


    public ApiResponse(String message, String token, boolean success, Sos sos, User user, Center center, ArrayList<Center> centers, Array errors) {
        this.message = message;
        this.token = token;
        this.success = success;
        this.sos = sos;
        this.user = user;
        this.center = center;
        this.centers = centers;
        this.errors = errors;
    }

    public ArrayList<Center> getCenters() {
        return centers;
    }

    public void setCenters(ArrayList<Center> centers) {
        this.centers = centers;
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

    public Sos getSos() {
        return sos;
    }

    public void setSos(Sos sos) {
        this.sos = sos;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    public Array getErrors() {
        return errors;
    }

    public void setErrors(Array errors) {
        this.errors = errors;
    }
}
