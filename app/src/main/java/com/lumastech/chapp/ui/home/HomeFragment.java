package com.lumastech.chapp.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lumastech.chapp.R;
import com.lumastech.chapp.Utility;
import com.lumastech.chapp.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private FragmentHomeBinding binding;
    GoogleMap map;
    MapView mapView;
    Utility utility;
    Context context;
    TextView centersTextView, emergenceTextView, myProfileTextView, mapsTextView;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng sydney = new LatLng(-15.414039823302584, 28.278973953534365);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Lusaka"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    };

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
//        googleMap = mapView;
        LatLng sydney = new LatLng(-15.414039823302584, 28.278973953534365);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Lusaka"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    // Define an interface for communication
    public interface OnFragmentNav {
        void onButtonClicked(int id);
    }

    private OnFragmentNav listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentNav) {
            listener = (OnFragmentNav) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        context = getContext();
        utility = new Utility(context);
        centersTextView = binding.centers;
        emergenceTextView = binding.emergence;
        myProfileTextView = binding.myProfile;
        mapsTextView = binding.maps;
        mapView = binding.mapView;
        try {
//            initilizeMap();
        } catch (Exception e) {
            utility.generalDialog(e.getLocalizedMessage());
        }

        centersTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onButtonClicked(R.id.nav_health_centers);
                }
            }
        });

        emergenceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onButtonClicked(R.id.nav_sos_settings);
                }
            }
        });

        myProfileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onButtonClicked(R.id.nav_profile);
                }
            }
        });

        mapsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onButtonClicked(R.id.nav_maps);
                }
            }
        });
        return root;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

//    private void initilizeMap() {
//        if (map == null) {
//            //googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
////            map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
//            map = mapView.getMapAsync(callback);
//
//            // check if map is created successfully or not
//            if (map == null) {
////                utility.generalDialog("Sorry! unable to create maps");
//            }
//        }
//    }
}