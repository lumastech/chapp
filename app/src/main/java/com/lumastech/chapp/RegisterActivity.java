package com.lumastech.chapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.gson.Gson;
import com.lumastech.chapp.Models.RegisterRequest;
import com.lumastech.chapp.Models.RegisterResponse;


import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText nameEdit;
    private EditText emailEdit = null;
    private EditText passwordEdit;
    private EditText passwordConfirmEdit;
    private Button registerBtn = null;
    Utility utility;
    Context context = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        context = RegisterActivity.this;
        utility  = new Utility(context);

        nameEdit = findViewById(R.id.name);
        emailEdit = findViewById(R.id.email);
        passwordEdit = findViewById(R.id.password);
        passwordConfirmEdit = findViewById(R.id.password_confirm);
        registerBtn = findViewById(R.id.register_btn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEdit.getText().toString();
                String email = emailEdit.getText().toString();
                String passwordConfirm = passwordConfirmEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                utility.removeError(nameEdit);
                utility.removeError(emailEdit);
                utility.removeError(passwordEdit);
                utility.removeError(passwordConfirmEdit);

                if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !passwordConfirm.isEmpty()){
                    if(!password.equals(passwordConfirm)){
                        utility.setError(passwordEdit);
                        utility.setError(passwordConfirmEdit);
                        Toast.makeText(context, "Password and Password Confirm Don't match!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    RegisterRequest request = new RegisterRequest();
                    request.setName(name);
                    request.setEmail(email);
                    request.setPassword(password);
                    registerUser(request);
                }else {
                    if (name.isEmpty()){
                        utility.setError(nameEdit);
                    }
                    if (email.isEmpty()){
                        utility.setError(emailEdit);
                    }
                    if (password.isEmpty()){
                        utility.setError(passwordEdit);
                    }
                    if (passwordConfirm.isEmpty()){
                        utility.setError(passwordConfirmEdit);
                    }
                }
            }
        });
    }

    public void registerUser(RegisterRequest request){
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
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
            Call<RegisterResponse> registerResponseCall = Api.apiCall().register(request);
            registerResponseCall.enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(@NonNull Call<RegisterResponse> call, @NonNull Response<RegisterResponse> response) {

                    if (response.isSuccessful()){
                        assert response.body() != null;
                        RegisterResponse res = response.body();
                        if (response.body().isSuccess()){
                        utility.writeToFile("token", response.body().getToken());
                        utility.writeToFile("user", new Gson().toJson(response.body().getUser()));
                        startActivity(new Intent(context, MainActivity.class));
                        }else{
                            String message = "";
                            if (res.getMessage() != null){
                                message = res.getMessage();
                            }
                            if (message.contains("name")){
                                utility.setError(nameEdit);
                            }
                            if (message.contains("email")){
                                utility.setError(emailEdit);
                            }
                            if (message.contains("password")){
                                utility.setError(passwordEdit);
                            }
                            if (!message.isEmpty()){
                                utility.generalDialog(message);
                            }
                        }
//
                        dialog.dismiss();
//                        finish();

                    }else {
                        dialog.dismiss();
                        utility.generalDialog("We received an expected response! Please try again");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RegisterResponse> call, @NonNull Throwable t) {
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
}