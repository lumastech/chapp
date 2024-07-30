package com.lumastech.chapp.ui.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.lumastech.chapp.Api;
import com.lumastech.chapp.LoginActivity;
import com.lumastech.chapp.MainActivity;
import com.lumastech.chapp.Models.ApiResponse;
import com.lumastech.chapp.Models.RegisterRequest;
import com.lumastech.chapp.Models.RegisterResponse;
import com.lumastech.chapp.Models.User;
import com.lumastech.chapp.R;
import com.lumastech.chapp.RegisterActivity;
import com.lumastech.chapp.Utility;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileUpdateActivity extends AppCompatActivity {
    private EditText nameEdit;
    private EditText phoneEdit;
    private EditText addressEdit;
    private EditText townEdit;
    private RadioGroup genderEdit = null;
    private RadioButton male, female;
    TextView email;
    Utility utility;
    Context context = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_update);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        context = ProfileUpdateActivity.this;
        utility  = new Utility(context);

        email = findViewById(R.id.email);
        nameEdit = findViewById(R.id.name);
        genderEdit = findViewById(R.id.gender);
        phoneEdit = findViewById(R.id.phone);
        addressEdit = findViewById(R.id.address);
        townEdit = findViewById(R.id.town);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        Button updateBtn = findViewById(R.id.update_btn);

        getProfile();

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gender = "";
                if (male.isChecked()){
                    gender = "male";
                } else if (female.isChecked()) {
                    gender = "female";
                }
                String name = nameEdit.getText().toString();
                String phone = phoneEdit.getText().toString();
                String address = addressEdit.getText().toString();
                String town = townEdit.getText().toString();

                utility.removeError(nameEdit);
                utility.removeError(genderEdit);
                utility.removeError(phoneEdit);
                utility.removeError(addressEdit);
                utility.removeError(townEdit);

                if(!name.isEmpty() && !gender.isEmpty() && !address.isEmpty() && !town.isEmpty() && !phone.isEmpty()){
                    Profile request = new Profile();
                    request.setName(name);
                    request.setGender(gender);
                    request.setAddress(address);
                    request.setTown(town);
                    request.setPhone(phone);
                    updateUser(request);
                }else {
                    if (name.isEmpty()){
                        utility.setError(nameEdit);
                    }
                    if (gender.isEmpty()){
                        utility.setError(genderEdit);
                    }
                    if (phone.isEmpty()){
                        utility.setError(phoneEdit);
                    }
                    if (address.isEmpty()){
                        utility.setError(addressEdit);
                    }
                    if (town.isEmpty()){
                        utility.setError(townEdit);
                    }
                }
            }
        });
    }

    public void getProfile(){
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
            Call<ApiResponse> registerResponseCall = Api.apiCall().profile(utility.readFile("token"));
            registerResponseCall.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                    dialog.dismiss();
                    if (response.isSuccessful()){
                        assert response.body() != null;
                        ApiResponse res = response.body();
                        if (response.body().isSuccess()){
                            utility.writeToFile("user", new Gson().toJson(response.body().getUser()));
                            displayInfo(response.body().getUser());
                        }else {
                            utility.generalDialog("Something went wrong!");
                        }

                    }else {
                        if (response.code() == 401){
                            startActivity(new Intent(ProfileUpdateActivity.this, LoginActivity.class));
                        } else {
                            utility.generalDialog("Error! Something went wrong. try again later");
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

    public void updateUser(Profile request){
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
            Call<ApiResponse> registerResponseCall = Api.apiCall().userUpdate(request, utility.readFile("token"));
            registerResponseCall.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                    dialog.dismiss();
                    if (response.isSuccessful()){
                        assert response.body() != null;
                        ApiResponse res = response.body();
                        if (response.body().isSuccess()){
                            utility.generalDialog("Information Updated!");
                            utility.writeToFile("user", new Gson().toJson(response.body().getUser()));
                            displayInfo(response.body().getUser());
                        }else{
                            String message = "";
                            if (res.getMessage() != null){
                                message = res.getMessage();
                            }
                            if (message.contains("name")){
                                utility.setError(nameEdit);
                            }
                            if (message.contains("sex")){
                                utility.setError(genderEdit);
                            }
                            if (message.contains("phone")){
                                utility.setError(phoneEdit);
                            }
                            if (message.contains("address")){
                                utility.setError(addressEdit);
                            }
                            if (message.contains("town")){
                                utility.setError(townEdit);
                            }

                            if (!message.isEmpty()){
                                utility.generalDialog(message);
                            }
                        }
                    }else {
                        dialog.dismiss();
                        if (response.code() == 401){
                            startActivity(new Intent(ProfileUpdateActivity.this, LoginActivity.class));
                        } else {
                            utility.generalDialog("Error! Something went wrong. try again later");
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

    public  void displayInfo(User user){
        try {
            email.setText(user.getEmail());
            nameEdit.setText(user.getName());
            phoneEdit.setText(user.getPhone());
            addressEdit.setText(user.getAddress());
            townEdit.setText(user.getTown());
            if(Objects.equals(user.getSex(), "male")){
                male.setChecked(true);
            } else if (Objects.equals(user.getSex(), "female")) {
                female.setChecked(true);
            }

        }catch (Exception e){
            utility.generalDialog(e.getLocalizedMessage());
        }
    }
}