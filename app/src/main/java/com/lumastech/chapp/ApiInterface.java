package com.lumastech.chapp;

import com.lumastech.chapp.Models.ApiResponse;
import com.lumastech.chapp.Models.Center;
import com.lumastech.chapp.Models.RegisterRequest;
import com.lumastech.chapp.Models.RegisterResponse;
import com.lumastech.chapp.Models.Sos;
import com.lumastech.chapp.ui.profile.Profile;

import java.lang.reflect.Array;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {
    // LOG IN
    @Headers({"Content-Type: application/json", "accept: application/json"})
    @POST("token/create")
    Call<RegisterResponse> token(@Body RegisterRequest data);


    // VERIFY TOKEN
    @Headers({"Content-Type: application/json", "accept: application/json"})
    @GET("token/verify")
    Call<ApiResponse> tokenVerify(@Header("Authorization") String token);

    // LOGOUT
    @Headers({"Content-Type: application/json", "accept: application/json"})
    @GET("logout")
    Call<ApiResponse> logout(@Header("Authorization") String token);

    // USER REGISTER
    @Headers({"Content-Type: application/json", "accept: application/json"})
    @POST("register")
    Call<RegisterResponse> register(@Body RegisterRequest data);

    // UPDATE PROFILE
    @Headers({"Content-Type: application/json", "accept: application/json"})
    @POST("profile/update")
    Call<ApiResponse> userUpdate(@Body Profile data, @Header("Authorization") String token);

    // GET PROFILE
    @Headers({"Content-Type: application/json", "accept: application/json"})
    @GET("profile")
    Call<ApiResponse> profile(@Header("Authorization") String token);

    // CREATE/UPDATE SOS
    @Headers({"Content-Type: application/json", "accept: application/json"})
    @POST("sos/create")
    Call<ApiResponse> sosCreate(@Body Sos data, @Header("Authorization") String token);

    // CREATE CENTER
    @Headers({"Content-Type: application/json", "accept: application/json"})
    @POST("center/create")
    Call<ApiResponse> centerCreate(@Body Center data, @Header("Authorization") String token);

    // UPDATE CENTER
    @Headers({"Content-Type: application/json", "accept: application/json"})
    @POST("center/update")
    Call<ApiResponse> centerUpdate(@Body Center data, @Header("Authorization") String token);

    // GET SOS
    @Headers({"Content-Type: application/json", "accept: application/json"})
    @GET("centers")
    Call<ApiResponse> centersGet(@Header("Authorization") String token);

    // SEND SOS EMERGENCE MESSAGE
    @Headers({"Content-Type: application/json", "accept: application/json"})
    @GET("sos/send")
    Call<ApiResponse> sosSend(@Header("Authorization") String token);

    // GET SOS
    @Headers({"Content-Type: application/json", "accept: application/json"})
    @GET("sos")
    Call<ApiResponse> sosGet(@Header("Authorization") String token);
}