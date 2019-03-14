package com.bcabuddies.fitsteps;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountRegister extends AppCompatActivity {



    private TextInputEditText userEmail, userPass, userCpass;
    private Button btnSubmit;
    private FirebaseAuth auth;
    private String email, password, confirmPass;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_register);

        userEmail = findViewById(R.id.regiter_emailEditText);
        userPass = findViewById(R.id.register_passEditText);
        userCpass =findViewById(R.id.register_CpassEditText);
        btnSubmit =findViewById(R.id.register_btnSubmit);
        auth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = userEmail.getText().toString();
                password = userPass.getText().toString();
                confirmPass = userCpass.getText().toString();

                if (email.equals("") || password.equals("")) {
                    Toast.makeText(AccountRegister.this, "Please. fill all info", Toast.LENGTH_SHORT).show();
                } else {
                    if (password.equals(confirmPass)) {
                        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(AccountRegister.this, "User created successfully", Toast.LENGTH_SHORT).show();
                                RegisterSecondFrag registerSecondFrag = new RegisterSecondFrag();
                                startActivity(new Intent(AccountRegister.this,RegisterMain.class));
                            }
                        });
                    } else {
                        Toast.makeText(AccountRegister.this, "Password did not match", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });





    }
}
