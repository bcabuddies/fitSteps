package com.bcabuddies.fitsteps;


import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFirstFrag extends Fragment {


    private TextInputEditText fullName, userEmail, userPass, userCpass;
    private Button btnSubmit;


    public RegisterFirstFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_first, container, false);

        fullName = view.findViewById(R.id.regiter_nameEditText);
        userEmail = view.findViewById(R.id.regiter_emailEditText);
        userPass = view.findViewById(R.id.register_passEditText);
        userCpass = view.findViewById(R.id.register_CpassEditText);
        btnSubmit = view.findViewById(R.id.register_btnSubmit);

        // Inflate the layout for this fragment
        return view;
    }

}
