package com.lumastech.chapp.ui.features;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lumastech.chapp.R;
import com.lumastech.chapp.Utility;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BmiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BmiFragment extends Fragment {

    EditText weight, height;
    TextView results;
    Utility utility;
    Button calculateBtn, clearBtn;

    public BmiFragment() {
        // Required empty public constructor
    }

    public static BmiFragment newInstance() {
        BmiFragment fragment = new BmiFragment();
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
        return inflater.inflate(R.layout.fragment_bmi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        weight = view.findViewById(R.id.weight);
        height = view.findViewById(R.id.height);
        results = view.findViewById(R.id.bmi_results);
        calculateBtn = view.findViewById(R.id.calculate_btn);
        clearBtn = view.findViewById(R.id.clear_btn);

        utility = new Utility(getContext());

        calculateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBmi();
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

    }

    public void calculateBmi(){
        utility.removeError(weight);
        utility.removeError(height);
        if (weight.getText().toString().isEmpty()){
            utility.setError(weight);
        }
        if (height.getText().toString().isEmpty()){
            utility.setError(height);
        }

        if (!height.getText().toString().isEmpty() && !weight.getText().toString().isEmpty()){
            try {
                float heightFloat = Float.parseFloat(height.getText().toString());
                float weightFloat = Float.parseFloat(weight.getText().toString());

                if (heightFloat > 0 && weightFloat > 0){
                    float res =  weightFloat/(heightFloat*heightFloat);
                    setResultsColor(res, results);
                    results.setText(String.format("%.2f", res));
                }
            }catch (Exception e){
                utility.generalDialog("Please input valid numbers : "+e.getLocalizedMessage());
            }
        }
    }

    public void setResultsColor(float res, View view){
        if (res <= 18.4){
            view.setBackgroundResource(R.color.yellow);
        }
        if (res >= 18.5 && res <= 24.9){
            view.setBackgroundResource(R.color.teal_700);
        }
        if (res >= 25.0 && res <= 39.9){
            view.setBackgroundResource(R.color.oragered);
        }
        if (res >= 40.0){
            view.setBackgroundResource(R.color.red);
        }
    }

    public void clear(){
        weight.setText("");
        height.setText("");
        results.setBackgroundResource(R.color.gray_light);
    }
}