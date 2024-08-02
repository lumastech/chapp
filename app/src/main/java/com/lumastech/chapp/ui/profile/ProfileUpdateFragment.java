package com.lumastech.chapp.ui.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lumastech.chapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileUpdateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileUpdateFragment extends Fragment {

    public ProfileUpdateFragment() {
        // Required empty public constructor
    }

    public static ProfileUpdateFragment newInstance() {
        ProfileUpdateFragment fragment = new ProfileUpdateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(getContext(), ProfileUpdateActivity.class));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_update, container, false);
    }
}