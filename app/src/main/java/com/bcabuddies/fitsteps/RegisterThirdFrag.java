package com.bcabuddies.fitsteps;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterThirdFrag extends Fragment {

    private EditText ageET, weightET, genderET, heightET;
    private RadioButton ecto, meso, endo;
    private Button submit;
    private String age, weight, gender, height, body = "";
    private Context context;
    private String uid;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore firebaseFirestore;

    public RegisterThirdFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_third, container, false);

        context = view.getContext();

        ageET = view.findViewById(R.id.reg3_ageText);
        weightET = view.findViewById(R.id.reg3_weightText);
        genderET = view.findViewById(R.id.reg3_genderText);
        heightET = view.findViewById(R.id.reg3_heightText);

        ecto = view.findViewById(R.id.reg3_radioEcto);
        meso = view.findViewById(R.id.reg3_radioMeso);
        endo = view.findViewById(R.id.reg3_radioEndo);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        uid = user.getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        submit = view.findViewById(R.id.reg3_btnSubmit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                age = ageET.getText().toString();
                weight = weightET.getText().toString();
                gender = genderET.getText().toString();
                height = heightET.getText().toString();

                if (ecto.isSelected())
                    body = "Ectomorph";
                if (meso.isSelected())
                    body = "Mesomorph";
                if (endo.isSelected())
                    body = "Endomorph";

                if (age.isEmpty() || weight.isEmpty() || gender.isEmpty() || height.isEmpty()) {
                    Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    if (body.isEmpty() || body.equals("")) {
                        Toast.makeText(context, "Please select body type", Toast.LENGTH_SHORT).show();
                    } else {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("age", age);
                        map.put("weight", weight);
                        map.put("gender", gender);
                        map.put("height", height);
                        map.put("body", body);
                        map.put("uid", uid);

                        firebaseFirestore.collection("Users").document(uid)
                                .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(context, "Details updated", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(context,Home.class);
                                    startActivity(i);
                                    getActivity().finish();
                                } else {
                                    Toast.makeText(context, "Some error "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });

        return view;
    }

}
