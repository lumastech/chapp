package com.lumastech.chapp.ui.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lumastech.chapp.R;
import com.lumastech.chapp.Utility;

import org.json.JSONObject;

import java.util.Objects;


public class ProfileFragment extends Fragment {

   TextView name, phone, email, gender, address, town;
   Button updateBtn;
   ImageView profilePicture;
   Utility utility = null;
   boolean notifyProfileUpdate = false;
    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        utility = new Utility(getContext());

        profilePicture = view.findViewById(R.id.profile_picture);
        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        email = view.findViewById(R.id.email);
        gender = view.findViewById(R.id.gender);
        address = view.findViewById(R.id.address);
        town = view.findViewById(R.id.town);
        updateBtn = view.findViewById(R.id.save_btn);

        utility.getProfile();
        profile();

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set profile edit fragment
                startActivity(new Intent(getContext(), ProfileUpdateActivity.class));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        profile();
    }

    public void profile(){
        name.setText(getValueFromJsom("name"));
        email.setText(getValueFromJsom("email"));
        phone.setText(getValueFromJsom("phone"));
        town.setText(getValueFromJsom("town"));
        address.setText(getValueFromJsom("address"));
        gender.setText(getValueFromJsom("sex"));
        if (notifyProfileUpdate){
            utility.generalDialog("Please update your profile!");
        }
    }

    public String getValueFromJsom(String string){
        String value = "";
        try {
            String profileText = utility.readFile("user");
            if(!Objects.equals(profileText, "null")) {
                JSONObject userObject = new JSONObject(profileText);
                value = userObject.getString(string);
            }
        }catch (Exception ignored){
            notifyProfileUpdate = true;
        }

        return  value;
    }
}