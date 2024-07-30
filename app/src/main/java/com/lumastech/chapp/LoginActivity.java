package com.lumastech.chapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText = null, passworEditText = null;
    private Button loginBtn = null;
    Utility utility;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        context = LoginActivity.this;
        utility = new Utility(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        usernameEditText = findViewById(R.id.username);
        passworEditText = findViewById(R.id.password);
        loginBtn = findViewById(R.id.login_btn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = usernameEditText.getText().toString();
                String password = passworEditText.getText().toString();

                utility.removeError(usernameEditText);
                utility.removeError(passworEditText);

                if(!email.isEmpty() && !password.isEmpty()){
                    RegisterRequest request = new RegisterRequest();
                    request.setEmail(email);
                    request.setPassword(password);
                    loginUser(request);
                }else {
                    if (email.isEmpty()){
                        utility.setError(usernameEditText);
                    }
                    if (password.isEmpty()){
                        utility.setError(passworEditText);
                    }
                }
            }
        });
    }


    public void loginUser(RegisterRequest request){
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
            Call<RegisterResponse> registerResponseCall = Api.apiCall().token(request);
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
                            if (message.contains("email")){
                                utility.setError(usernameEditText);
                            }
                            if (message.contains("password")){
                                utility.setError(passworEditText);
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