package com.lumastech.chapp;

import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.view.View;

import androidx.annotation.NonNull;

import com.lumastech.chapp.Models.ApiResponse;
import com.lumastech.chapp.ui.profile.Profile;
import com.lumastech.chapp.ui.profile.ProfileUpdateActivity;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utility {
    private final Context context;

    public Utility(Context context) {
        this.context = context;
    }

    // WRITE TO FILE
    public void writeToFile(String filename, String content) {
        SharedPreferences mPrefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(filename, content);
        editor.apply();
    }

    public String readFile(String filename){
        SharedPreferences sharedPreferences = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(filename, "null");
    }

    public void logout(String base){
        writeToFile("token", "null");
        startActivity(context, new Intent(context, LoginActivity.class), null);
    }
    
    public void authCheck() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        });
        androidx.appcompat.app.AlertDialog alertDialog;

        alertDialog = builder.create();
//        ProgressDialog dialog = ProgressDialog.show(context, "",
//                "Please wait...", true);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(context, ConnectivityManager.class);
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
            Call<ApiResponse> tokenResponseCall = Api.apiCall().tokenVerify(readFile("token"));
            tokenResponseCall.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {

//                    dialog.dismiss();
                    if (response.isSuccessful()){
                        assert response.body() != null;
                        if (response.body().isSuccess()){
                            startActivity(context, new Intent(context, MainActivity.class), null);
                        }else {
                            startActivity(context, new Intent(context, WelcomeActivity.class), null);
                        }
                    }else {
                        startActivity(context, new Intent(context, WelcomeActivity.class), null);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
//                    dialog.dismiss();
                    startActivity(context, new Intent(context, WelcomeActivity.class), null);
                }
            });
        }else{
//            dialog.dismiss();
            startActivity(context, new Intent(context, WelcomeActivity.class), null);
        }
    }

    public void logout() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        });
        androidx.appcompat.app.AlertDialog alertDialog;

        alertDialog = builder.create();
        ProgressDialog dialog = ProgressDialog.show(context, "",
                "Please wait...", true);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(context, ConnectivityManager.class);
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
            Call<ApiResponse> tokenResponseCall = Api.apiCall().logout(readFile("token"));
            tokenResponseCall.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {

                    dialog.dismiss();
                    if (response.isSuccessful()){
                        assert response.body() != null;
                        if (response.body().isSuccess()){
                            startActivity(context, new Intent(context, WelcomeActivity.class), null);
                        }
                    }else {
                        startActivity(context, new Intent(context, WelcomeActivity.class), null);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                    dialog.dismiss();
                    startActivity(context, new Intent(context, WelcomeActivity.class), null);
                }
            });
        }else{
            dialog.dismiss();
            startActivity(context, new Intent(context, WelcomeActivity.class), null);
        }
    }

    public void generalDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        });
        builder.setMessage(message);
        AlertDialog alertDialog;
        alertDialog = builder.create();
        alertDialog.show();
    }

    public Profile getProfile(){
        Profile profile = new Profile();
        return profile;
    }

    public void setError(View view){
        view.setBackgroundResource(R.drawable.bg_input_error);
    }

    public void removeError(View view){
        view.setBackgroundResource(R.drawable.bg_oultline);
    }

    public void sendSos(){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        });
        androidx.appcompat.app.AlertDialog alertDialog;

        alertDialog = builder.create();
        ProgressDialog dialog = ProgressDialog.show(context, "",
                "Please wait...", true);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(context, ConnectivityManager.class);
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
            Call<ApiResponse> tokenResponseCall = Api.apiCall().sosSend(readFile("token"));
            tokenResponseCall.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                    dialog.dismiss();

                    try{
                        if (response.isSuccessful()){
                            assert response.body() != null;
                            if (response.body().isSuccess()){
                                generalDialog(response.body().getMessage());
                            }else {
                                generalDialog(response.body().getMessage());
                            }
                        }else {
                            if (response.code() == 401){
                                startActivity(context, new Intent(context, LoginActivity.class), null);
                            } else {
                                generalDialog("Error! Something went wrong. try again later");
                            }
                        }
                    }catch (Exception e){
                        generalDialog(e.getLocalizedMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
//
                    startActivity(context, new Intent(context, WelcomeActivity.class), null);
                }
            });
        }else{
//            dialog.dismiss();
            startActivity(context, new Intent(context, WelcomeActivity.class), null);
        }
    }
    
}
