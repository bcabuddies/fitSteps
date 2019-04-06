package com.bcabuddies.fitsteps;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Welcome extends AppCompatActivity {


    private static final String TAG = "Welcome.java";
    private static final int RC_SIGN_IN = 1;
    TextView txtReg, txtForgotPass;
    SignInButton googleSignInBtn;
    String fName, profUrl;
    private FirebaseAuth auth;
    private String email, password;
    private TextInputEditText userEmail, userPass;
    private Button btnLogin;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.WelcomeAppTheme);
        Window mWindow = getWindow();
        mWindow.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        auth = FirebaseAuth.getInstance();
        userEmail = findViewById(R.id.login_emailEditText);
        userPass = findViewById(R.id.login_passwordEditText);
        btnLogin = findViewById(R.id.login_loginBtn);
        txtReg = findViewById(R.id.login_register);
        txtForgotPass = findViewById(R.id.login_forgetPassword);
        googleSignInBtn = findViewById(R.id.login_googlebtn);
        mAuthListener = firebaseAuth -> {

            if (firebaseAuth.getCurrentUser() != null) {
                Log.e("checkKey", "Main onAuthStateChanged: ");
                startActivity(new Intent(Welcome.this, StepsMain.class));
                finish();
            }

        };

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        txtReg.setOnClickListener(v -> {
            Intent sharedIntent = new Intent(Welcome.this, AccountRegister.class);
            Pair[] pairs = new Pair[3];
            pairs[0] = new Pair<View, String>(userEmail, "email_transition");
            pairs[1] = new Pair<View, String>(userPass, "password_transition");
            pairs[2] = new Pair<View, String>(btnLogin, "btnLogin_transition");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Welcome.this, pairs);
            startActivity(sharedIntent, options.toBundle());
        });

        txtForgotPass.setOnClickListener(v -> {
            Intent sharedIntent = new Intent(Welcome.this, ForgotPass.class);
            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View, String>(userEmail, "email_transition");
            pairs[1] = new Pair<View, String>(btnLogin, "btnLogin_transition");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Welcome.this, pairs);
            startActivity(sharedIntent, options.toBundle());
        });

        googleSignInBtn.setOnClickListener(v -> signIn());

        btnLogin.setOnClickListener(v -> {

            email = Objects.requireNonNull(userEmail.getText()).toString();
            password = Objects.requireNonNull(userPass.getText()).toString();
            if (!(email.equals("") || password.equals(""))) {
                auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
                    Toast.makeText(Welcome.this, "Successfully Logged in", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Welcome.this, StepsMain.class));
                    finish();
                }).addOnFailureListener(e -> Toast.makeText(Welcome.this, "Incorrect email or password", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(Welcome.this, "Enter email and password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account);
                fName = account.getDisplayName();
                profUrl = Objects.requireNonNull(account.getPhotoUrl()).toString();
                profUrl = profUrl.substring(0, profUrl.length() - 15) + "s400-c/photo.jpg";
                Log.e("googleRet", "name: " + fName);
                Log.e("googleRet", "profile: " + profUrl);
                Log.v("mGoogleSignIn", "Google sign in try");
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.v("GoogleSignIn", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        Log.v("GoogleSignIn", "firebaseAuthWithGoogle: " + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "onComplete: myException: " + Objects.requireNonNull(task.getException()).toString());
                        try {
                            Log.e(TAG, "onComplete: googleSign try");
                            Toast.makeText(Welcome.this, "Login Error", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e(TAG, "onComplete: googleSign catch");
                            Toast.makeText(Welcome.this, "login error", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    } else {
                        Log.e(TAG, "onComplete: googleSign else");
                        Toast.makeText(Welcome.this, "login success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Welcome.this, PostRegisterFirst.class);
                        intent.putExtra("name", fName);
                        intent.putExtra("profUrl", profUrl);
                        startActivity(intent);
                    }
                });
    }
}