package com.bcabuddies.fitsteps;


import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFirstFrag extends Fragment {


    private TextInputEditText userEmail, userPass, userCpass;
    private Button btnSubmit;
    private FirebaseAuth auth;
    private String email, password, confirmPass;
    private FirebaseFirestore firebaseFirestore;
    private String userId;


    public RegisterFirstFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_first, container, false);


        userEmail = view.findViewById(R.id.regiter_emailEditText);
        userPass = view.findViewById(R.id.register_passEditText);
        userCpass = view.findViewById(R.id.register_CpassEditText);
        btnSubmit = view.findViewById(R.id.register_btnSubmit);
        auth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = userEmail.getText().toString();
                password = userPass.getText().toString();
                confirmPass = userCpass.getText().toString();


                if (email.equals("") || password.equals("")) {
                    Toast.makeText(getContext(), "Please fill all info.", Toast.LENGTH_SHORT).show();
                } else {
                    if (password.equals(confirmPass)) {

                        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                Toast.makeText(getContext(), "User created successfully", Toast.LENGTH_SHORT).show();
                                RegisterSecondFrag registerSecondFrag = new RegisterSecondFrag();
                                getFragmentManager().beginTransaction().replace(R.id.registerMain_frame, registerSecondFrag).commit();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Password did not match", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });


        return view;
    }

}
