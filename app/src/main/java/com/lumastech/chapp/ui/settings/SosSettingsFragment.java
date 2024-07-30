package com.lumastech.chapp.ui.settings;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lumastech.chapp.Api;
import com.lumastech.chapp.LoginActivity;
import com.lumastech.chapp.MainActivity;
import com.lumastech.chapp.Models.ApiResponse;
import com.lumastech.chapp.Models.RegisterResponse;
import com.lumastech.chapp.Models.Sos;
import com.lumastech.chapp.R;
import com.lumastech.chapp.Utility;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SosSettingsFragment extends Fragment {
    TextView name, email, phone, message;
    Button saveBtn;
    Utility utility;
    Context context;
    Sos sos;
    public SosSettingsFragment() {
        // Required empty public constructor
    }

    public static SosSettingsFragment newInstance() {
        SosSettingsFragment fragment = new SosSettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        utility = new Utility(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sos_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);
        message = view.findViewById(R.id.message);
        saveBtn = view.findViewById(R.id.save_btn);
        getSos();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isClean = true;
                Sos sos = new Sos();
                if(name.getText().toString().isEmpty()){
                    utility.setError(name);
                    isClean = false;
                }
                if(email.getText().toString().isEmpty()){
                    isClean = false;
                    utility.setError(email);
                }
                if(phone.getText().toString().isEmpty()){
                    isClean = false;
                    utility.setError(phone);
                }
                if(message.getText().toString().isEmpty()){
                    isClean = false;
                    utility.setError(message);
                }

                if(isClean){
                    sos.setName(name.getText().toString());
                    sos.setEmail(email.getText().toString());
                    sos.setPhone(phone.getText().toString());
                    sos.setMessage(message.getText().toString());
                    utility.removeError(name);
                    utility.removeError(email);
                    utility.removeError(phone);
                    utility.removeError(message);
                    postSos(sos);
                }
            }
        });
    }

    public void postSos(Sos sos){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        });
        AlertDialog alertDialog;

        alertDialog = builder.create();
        ProgressDialog dialog = ProgressDialog.show(context, "",
                "Please wait...", true);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(context, ConnectivityManager.class);
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
            Call<ApiResponse> sosResponseCall = Api.apiCall().sosCreate(sos, utility.readFile("token"));
            sosResponseCall.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                    dialog.dismiss();
                    if (response.isSuccessful()){
                        assert response.body() != null;
                        ApiResponse res = response.body();
                        if (response.body().isSuccess()){
                            utility.generalDialog("Information Updated!");
                            setSosViews(response.body().getSos());
                        }else{
                            String resMessage = "";
                            if (res.getMessage() != null){
                                resMessage = res.getMessage();
                            }
                            if (resMessage.contains("email")){
                                utility.setError(email);
                            }
                            if (resMessage.contains("phone")){
                                utility.setError(phone);
                            }
                            if (resMessage.contains("name")){
                                utility.setError(name);
                            }
                            if (resMessage.contains("message")){
                                utility.setError(message);
                            }
                            if (!resMessage.isEmpty()){
                                utility.generalDialog(resMessage);
                            }
                        }
                    }else {
                        dialog.dismiss();
                        if (response.code() == 401){
                            startActivity(new Intent(getContext(), LoginActivity.class));
                        } else {
                            utility.generalDialog("We received an expected response! Please try again");
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                    String message = "Error : "+t.getLocalizedMessage();
                    if (Objects.equals(t.getLocalizedMessage(), "End of input at line 1 column 1 path $")){
                        message = "We are having trouble connecting to the internet! Please make sure you have a working Internet connection.";
                    }
                    utility.generalDialog(message);
                    dialog.dismiss();
                    builder.setMessage(message);
                    alertDialog.show();
                }
            });
        }else{
            dialog.dismiss();
            utility.generalDialog("There is no Internet connection!");
        }
    }

    public void getSos(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        });
        AlertDialog alertDialog;

        alertDialog = builder.create();
        ProgressDialog dialog = ProgressDialog.show(context, "",
                "Please wait...", true);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(context, ConnectivityManager.class);
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
            Call<ApiResponse> sosResponseCall = Api.apiCall().sosGet(utility.readFile("token"));
            sosResponseCall.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                   try {
                       dialog.dismiss();

                       if (response.isSuccessful()){
                           assert response.body() != null;
                           if (response.body().isSuccess()){
                               setSosViews(response.body().getSos());
                           }
                       }else {
                           if (response.code() == 401){
                               startActivity(new Intent(getContext(), LoginActivity.class));
                           } else {
                               utility.generalDialog("Error! Something went wrong. try again later");
                           }
                       }
                   } catch (Exception e){
                       utility.generalDialog(e.getLocalizedMessage());
                   }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                    String message = "Error : "+t.getLocalizedMessage();
                    if (Objects.equals(t.getLocalizedMessage(), "End of input at line 1 column 1 path $")){
                        message = "We are having trouble connecting to the internet! Please make sure you have a working Internet connection.";
                    }
                    utility.generalDialog(message);
                    dialog.dismiss();
                    builder.setMessage(message);
                    alertDialog.show();
                }
            });
        }else{
            dialog.dismiss();
            utility.generalDialog("There is no Internet connection!");
        }
    }

    public void setSosViews(Sos sos){
        try {
            name.setText(sos.getName());
            email.setText(sos.getEmail());
            phone.setText(sos.getPhone());
            message.setText(sos.getMessage());
        }catch (Exception e){
            utility.generalDialog(e.getLocalizedMessage());
        }
    }

}