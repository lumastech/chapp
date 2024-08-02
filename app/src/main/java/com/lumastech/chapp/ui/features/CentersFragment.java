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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lumastech.chapp.Api;
import com.lumastech.chapp.LoginActivity;
import com.lumastech.chapp.Models.ApiResponse;
import com.lumastech.chapp.Models.Center;
import com.lumastech.chapp.R;
import com.lumastech.chapp.Utility;
import com.lumastech.chapp.ui.home.HomeFragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CentersFragment extends Fragment {

    private RecyclerView centersListView;
    private EditText searchInput;
    private Context context;
    private Utility utility;
    private ArrayList<Center> centerLister = new ArrayList<>();
    private  CenterAdapter centerAdapter;
    SelectListener listener;

    public CentersFragment() {
        // Required empty public constructor
    }

    public static CentersFragment newInstance() {
        CentersFragment fragment = new CentersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    // Define an interface for communication
    public interface OnFragmentNav {
        void onButtonClicked(int id);
    }

    private HomeFragment.OnFragmentNav navListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HomeFragment.OnFragmentNav) {
            navListener = (HomeFragment.OnFragmentNav) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_centers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        utility = new Utility(context);
        getCenters();
        centersListView = view.findViewById(R.id.centers_list_view);
        centersListView.setLayoutManager(new LinearLayoutManager(context));
        searchInput = view.findViewById(R.id.search_edit_text);

        final Button addCenter = view.findViewById(R.id.add_center_btn);
        addCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CenterAddActivity.class));
            }
        });
    }

    public void getCenters(){
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
                            listener = new SelectListener() {
                                @Override
                                public void onItemClicked(Center center) {
                                    Intent i = new Intent(context, CenterUpdateActivity.class);
                                    i.putExtra("id", center.getId());
                                    i.putExtra("name", center.getName());
                                    i.putExtra("email", center.getEmail());
                                    i.putExtra("phone", center.getName());
                                    i.putExtra("address", center.getAddress());
                                    i.putExtra("description", center.getDescription());
                                    i.putExtra("lat", center.getLat());
                                    i.putExtra("lng", center.getLng());
                                    startActivity(i);
//                                    utility.writeToFile("center", center.toString());
//                                    if (navListener != null) {
//                                        navListener.onButtonClicked(R.id.nav_health_centers);
//                                    }
                                }
                            };
                            centerLister = new ArrayList<>(response.body().getCenters());
                            centerAdapter = new CenterAdapter(listener,centerLister, context);
                            centersListView.setAdapter(centerAdapter);
                        }else{
                            String message = "Something went wrong!";
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
}