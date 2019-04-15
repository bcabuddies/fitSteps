package com.bcabuddies.fitsteps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

public class PostRegisterSecond extends AppCompatActivity {

    private TextInputLayout ageET, weightET, heightET, genderET;
    private RadioButton ecto, meso, endo;
    private Button submit;
    private String age = "", weight = "", gender = "", height = "", body = "";
    private String uid;
    private FirebaseFirestore firebaseFirestore;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_register_second);

        initView();

        //retrieving preData
        firebaseFirestore.collection("Users").document(uid).collection("user_data").document(uid).get().addOnCompleteListener(task -> {
            if (Objects.requireNonNull(task.getResult()).exists()) {
                String age, weight, gender, height, body;
                age = task.getResult().getString("age");
                Objects.requireNonNull(ageET.getEditText()).setText(age);
                weight = task.getResult().getString("weight");
                Objects.requireNonNull(weightET.getEditText()).setText(weight);
                gender = task.getResult().getString("gender");
                Objects.requireNonNull(genderET.getEditText()).setText(gender);
                height = task.getResult().getString("height");
                Objects.requireNonNull(heightET.getEditText()).setText(height);
                body = task.getResult().getString("body");
                assert body != null;
                if (body.equals("Mesomorph")) {
                    meso.setChecked(true);
                }
                if (body.equals("Ectomorph")) {
                    ecto.setChecked(true);
                }
                if (body.equals("Endomorph")) {
                    endo.setChecked(true);
                }
            } else {
                Objects.requireNonNull(ageET.getEditText()).setText("");
                Objects.requireNonNull(weightET.getEditText()).setText("");
                Objects.requireNonNull(genderET.getEditText()).setText("Gender");
                Objects.requireNonNull(heightET.getEditText()).setText("");
                meso.setChecked(true);
            }
        });


        genderET.setOnClickListener(v -> {
            Log.e("gender", "onClick: ");
            final PopupMenu genderMenu = new PopupMenu(PostRegisterSecond.this, genderET);
            genderMenu.getMenuInflater().inflate(R.menu.gender_menu, genderMenu.getMenu());
            genderMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.gender_male:
                        gender = "Male";
                        Objects.requireNonNull(genderET.getEditText()).setText("Male");
                        break;
                    case R.id.gender_female:
                        gender = "Female";
                        Objects.requireNonNull(genderET.getEditText()).setText("Female");
                        break;
                }

                return true;
            });
            genderMenu.show();
        });

        submit.setOnClickListener(v -> {
            age = Objects.requireNonNull(ageET.getEditText()).getText().toString();
            weight = Objects.requireNonNull(weightET.getEditText()).getText().toString();
            height = Objects.requireNonNull(heightET.getEditText()).getText().toString();
            gender = Objects.requireNonNull(genderET.getEditText()).getText().toString();
            body = "Endomorph";
            if (ecto.isChecked())
                body = "Ectomorph";
            if (meso.isChecked())
                body = "Mesomorph";
            if (endo.isChecked())
                body = "Endomorph";
            if (age.isEmpty() || weight.isEmpty() || gender.isEmpty() || height.isEmpty()) {
                Toast.makeText(PostRegisterSecond.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            } else {
                if (body.isEmpty()) {

                    Toast.makeText(PostRegisterSecond.this, "Please select body type", Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("age", age);
                    map.put("weight", weight);
                    map.put("gender", gender);
                    map.put("height", height);
                    map.put("body", body);
                    firebaseFirestore.collection("Users").document(uid).collection("user_data").document(uid)
                            .set(map).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(PostRegisterSecond.this, "Details updated", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(PostRegisterSecond.this, StepsMain.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(PostRegisterSecond.this, "Some error " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


    }

    private void initView() {
        ageET = findViewById(R.id.reg3_agelayout);
        weightET = findViewById(R.id.reg3_weightlayout);
        genderET = findViewById(R.id.reg3_genderTextlayout);
        heightET = findViewById(R.id.reg3_heightlayout);
        ecto = findViewById(R.id.reg3_radioEcto);
        meso = findViewById(R.id.reg3_radioMeso);
        endo = findViewById(R.id.reg3_radioEndo);
        submit = findViewById(R.id.reg3_btnSubmit);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        uid = user.getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
}
