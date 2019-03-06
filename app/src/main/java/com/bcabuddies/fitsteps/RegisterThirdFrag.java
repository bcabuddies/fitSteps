package com.bcabuddies.fitsteps;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterThirdFrag extends Fragment {

    private EditText ageET, weightET, heightET;
    private TextView genderET;
    private RadioButton ecto, meso, endo;
    private String age, weight, gender, height, body = "";
    private Context context;
    private String uid;
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

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        uid = user.getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Users").document(uid).collection("user_data").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    String age,weight,gender,height,body;

                    age=task.getResult().getString("age");
                    ageET.setText(age);

                    weight=task.getResult().getString("weight");
                    weightET.setText(weight);

                    gender=task.getResult().getString("gender");
                    genderET.setText(gender);

                    height=task.getResult().getString("height");
                    heightET.setText(height);

                    body=task.getResult().getString("body");
                    if (body.equals("Mesomorph")){
                        meso.setChecked(true);
                    }
                    if (body.equals("Ectomorph")){
                        ecto.setChecked(true);
                    }
                    if (body.equals("Endomorph")){
                        endo.setChecked(true);
                    }

                }
                else{
                    Toast.makeText(context, "No record found please fill the data", Toast.LENGTH_SHORT).show();
                }

            }
        });

        genderET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("gender", "onClick: " );
                final PopupMenu genderMenu = new PopupMenu(context, genderET);
                genderMenu.getMenuInflater().inflate(R.menu.gender_menu,genderMenu.getMenu());
                genderMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId() == R.id.gender_male) {
                            gender = "Male";
                            genderET.setText("Male");
                            return true;
                        }

                        if (item.getItemId() == R.id.gender_female) {
                            gender = "Female";
                            genderET.setText("Female");
                            return true;
                        }

                        return false;
                    }
                });

                genderMenu.show();
            }
        });

        Button submit = view.findViewById(R.id.reg3_btnSubmit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                age = ageET.getText().toString();
                weight = weightET.getText().toString();
                height = heightET.getText().toString();

                body = "Endomorph";

                if (ecto.isChecked())
                    body = "Ectomorph";
                if (meso.isChecked())
                    body = "Mesomorph";
                if (endo.isChecked())
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

                        firebaseFirestore.collection("Users").document(uid).collection("user_data").document(uid)
                                .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Details updated", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(context, Home.class);
                                    startActivity(i);
                                    getActivity().finish();
                                } else {
                                    Toast.makeText(context, "Some error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
