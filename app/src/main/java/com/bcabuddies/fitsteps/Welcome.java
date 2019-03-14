package com.bcabuddies.fitsteps;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class Welcome extends AppCompatActivity {


    private static final String TAG = "Welcome.java";
    private static final int RC_SIGN_IN = 1;
    TextView txtReg, txtForgotPass;
    SignInButton googleSignInBtn;
    String fNname, profUrl;
    private Window mWindow;
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
        mWindow = getWindow();
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
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {
                    Log.e("checkey", "Main onAuthStateChanged: ");
                    startActivity(new Intent(Welcome.this, Home.class));
                    finish();
                }

            }
        };

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        txtReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharedIntent = new Intent(Welcome.this, AccountRegister.class);
                Pair[] pairs = new Pair[3];
                pairs[0] = new Pair<View, String>(userEmail, "email_transition");
                pairs[1] = new Pair<View, String>(userPass, "password_transition");
                pairs[2] = new Pair<View, String>(btnLogin, "btnLogin_transition");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Welcome.this, pairs);
                startActivity(sharedIntent, options.toBundle());
            }
        });

        txtForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharedIntent = new Intent(Welcome.this, ForgotPass.class);
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(userEmail, "email_transition");
                pairs[1] = new Pair<View, String>(btnLogin, "btnLogin_transition");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Welcome.this, pairs);
                startActivity(sharedIntent, options.toBundle());
            }
        });

        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = userEmail.getText().toString();
                password = userPass.getText().toString();
                if (!(email.equals("") || password.equals(""))) {
                    auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(Welcome.this, "Sucessfully Logged in", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Welcome.this, Home.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Welcome.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(Welcome.this, "Enter email and password", Toast.LENGTH_SHORT).show();
                }
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
                firebaseAuthWithGoogle(account);
                fNname = account.getDisplayName();
                profUrl = account.getPhotoUrl().toString();
                profUrl = profUrl.substring(0, profUrl.length() - 15) + "s400-c/photo.jpg";
                Log.e("googleRet", "name: " + fNname);
                Log.e("googleRet", "pofile: " + profUrl);
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
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "onComplete: myexception: " + task.getException().toString());
                            try {
                                Log.e(TAG, "onComplete: googlesign try");
                                Toast.makeText(Welcome.this, "Login Error", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.e(TAG, "onComplete: googlesign catch");
                                Toast.makeText(Welcome.this, "login erorr", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        } else {
                            Log.e(TAG, "onComplete: googlesign else");
                            Toast.makeText(Welcome.this, "login success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Welcome.this, PostRegisterFirst.class);
                            intent.putExtra("name", fNname);
                            intent.putExtra("profUrl", profUrl);
                            startActivity(intent);
                        }
                    }
                });
    }
}