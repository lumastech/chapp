package com.lumastech.chapp.ui.features;

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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lumastech.chapp.Api;
import com.lumastech.chapp.LoginActivity;
import com.lumastech.chapp.Models.ApiResponse;
import com.lumastech.chapp.Models.Center;
import com.lumastech.chapp.R;
import com.lumastech.chapp.Utility;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsFragment extends Fragment {
    Context context;
    Utility utility;
    ArrayList<Center> centerLister = new ArrayList<>();
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            getCenters(googleMap);
            LatLng sydney = new LatLng(-15.414039823302584, 28.278973953534365);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(11.0f));
//            googleMap.setZ
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        utility = new Utility(context);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    public void getCenters(GoogleMap googleMap){
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
            Call<ApiResponse> registerResponseCall = Api.apiCall().centersGet(utility.readFile("token"));
            registerResponseCall.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                    dialog.dismiss();
                    if (response.isSuccessful()){
                        assert response.body() != null;
                        ApiResponse res = response.body();
                        if (response.body().isSuccess()){
                            centerLister = new ArrayList<>(response.body().getCenters());
                            for (int i=0; i<centerLister.size(); i++) {
                                if(centerLister.get(i).getLat() == null || centerLister.get(i).getLng() == null){
                                    continue;
                                }
                                double lat = Double.parseDouble(centerLister.get(i).getLat());
                                double lng = Double.parseDouble(centerLister.get(i).getLng());
                                String label = centerLister.get(i).getName();

                                if (validateLatLng(lat, lng)){
                                    LatLng sydney = new LatLng(lat, lng);
                                    googleMap.addMarker(new MarkerOptions().position(sydney).title(label));
                                }
                            }
                            try {
                            }catch (Exception e){
                                utility.generalDialog(e.getLocalizedMessage());
                            }
                        }else{
                            String message = "Something went wrong! WE couldn't get the Health centers list.";
                            if (res.getMessage() != null){
                                message = res.getMessage();
                            }
                            utility.generalDialog(message);
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

    public static boolean validateLatLng(double latitude, double longitude) {
        return (latitude >= -90.0 && latitude <= 90.0) &&
                (longitude >= -180.0 && longitude <= 180.0);
    }
}