package com.lumastech.chapp.ui.features;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.lumastech.chapp.Api;
import com.lumastech.chapp.LoginActivity;
import com.lumastech.chapp.Models.ApiResponse;
import com.lumastech.chapp.Models.Center;
import com.lumastech.chapp.R;
import com.lumastech.chapp.Utility;
import com.lumastech.chapp.ui.profile.Profile;
import com.lumastech.chapp.ui.profile.ProfileUpdateActivity;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CenterAddActivity extends AppCompatActivity {
    // One Button
    Button BSelectImage, postBtn;
    ImageView IVPreviewImage;
    Context context;
    Utility utility;
    private EditText nameEdit;
    private EditText phoneEdit;
    private EditText addressEdit;
    private EditText coordinatesEdit;
    private EditText descriptionEdit;
    private EditText emailEdit;
    private EditText latEdit;
    private EditText lnglEdit;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView getCoordinateView;


    int SELECT_PICTURE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_center_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        context = CenterAddActivity.this;
        utility = new Utility(context);
        BSelectImage = findViewById(R.id.BSelectImage);
        IVPreviewImage = findViewById(R.id.IVPreviewImage);
        getCoordinateView = findViewById(R.id.get_coordinate_btn);

        emailEdit = findViewById(R.id.email);
        nameEdit = findViewById(R.id.name);
        phoneEdit = findViewById(R.id.phone);
        addressEdit = findViewById(R.id.address);
        latEdit = findViewById(R.id.lat);
        lnglEdit = findViewById(R.id.lng);
        descriptionEdit = findViewById(R.id.description);

        postBtn = findViewById(R.id.update_btn);

        BSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        getCoordinates();

        getCoordinateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCoordinates();
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameEdit.getText().toString();
                String email = emailEdit.getText().toString();
                String phone = phoneEdit.getText().toString();
                String address = addressEdit.getText().toString();
                String lat = latEdit.getText().toString();
                String lng = lnglEdit.getText().toString();
                String description = descriptionEdit.getText().toString();

                utility.removeError(nameEdit);
                utility.removeError(emailEdit);
                utility.removeError(phoneEdit);
                utility.removeError(addressEdit);
                utility.removeError(latEdit);
                utility.removeError(lnglEdit);
                utility.removeError(descriptionEdit);

                if (!name.isEmpty() && !email.isEmpty() && !address.isEmpty() && !description.isEmpty() && !phone.isEmpty()) {
                    Center request = new Center();
                    request.setName(name);
                    request.setEmail(email);
                    request.setAddress(address);
                    request.setLat(lat);
                    request.setLng(lng);
                    request.setDescription(description);
                    request.setPhone(phone);
                    postCenter(request);
                } else {
                    if (name.isEmpty()) {
                        utility.setError(nameEdit);
                    }
                    if (address.isEmpty()) {
                        utility.setError(addressEdit);
                    }
                    if (phone.isEmpty()) {
                        utility.setError(phoneEdit);
                    }
                    if (address.isEmpty()) {
                        utility.setError(addressEdit);
                    }
                    if (lat.isEmpty() || lng.isEmpty()) {
                        getCoordinates();
                    }
                    if (description.isEmpty()) {
                        utility.setError(descriptionEdit);
                    }
                }
            }
        });

        getCoordinates();
    }

    private void getCoordinates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityResultLauncher<String[]> locationPermissionRequest =
                    registerForActivityResult(new ActivityResultContracts
                                    .RequestMultiplePermissions(), result -> {
                                Boolean fineLocationGranted = result.getOrDefault(
                                        Manifest.permission.ACCESS_FINE_LOCATION, false);
                                Boolean coarseLocationGranted = result.getOrDefault(
                                        Manifest.permission.ACCESS_COARSE_LOCATION,false);
                                if (fineLocationGranted != null && fineLocationGranted) {
                                    // Precise location access granted.
                                } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                    // Only approximate location access granted.
                                } else {
                                    utility.generalDialog("please give the app location permission and enable location");
                                }
                            }
                    );

            locationPermissionRequest.launch(new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });

            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            latEdit.setText(String.valueOf(location.getLatitude()));
                            lnglEdit.setText(String.valueOf(location.getLongitude()));
                        }
                    }
                });

    }


    void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    IVPreviewImage.setImageURI(selectedImageUri);
                }
            }
        }
    }

    public void postCenter(Center request){
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
            Call<ApiResponse> registerResponseCall = Api.apiCall().centerCreate(request, utility.readFile("token"));
            registerResponseCall.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                    dialog.dismiss();
                    if (response.isSuccessful()){
                        assert response.body() != null;
                        ApiResponse res = response.body();
                        if (response.body().isSuccess()){
                            utility.generalDialog("Information Updated!");
                            emailEdit.setText("");
                            nameEdit.setText("");
                            phoneEdit.setText("");
                            addressEdit.setText("");
                            descriptionEdit.setText("");
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
                            if (message.contains("phone")){
                                utility.setError(phoneEdit);
                            }
                            if (message.contains("address")){
                                utility.setError(addressEdit);
                            }
                            if (message.contains("coordinates")){
                                utility.setError(coordinatesEdit);
                            }
                            if (message.contains("description")){
                                utility.setError(descriptionEdit);
                            }

                            if (!message.isEmpty()){
                                utility.generalDialog(message);
                            }
                        }
                    }else {
                        dialog.dismiss();
                        if (response.code() == 401){
                            startActivity(new Intent(context, LoginActivity.class));
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
}