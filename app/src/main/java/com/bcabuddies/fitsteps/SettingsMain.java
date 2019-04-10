package com.bcabuddies.fitsteps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsMain extends AppCompatActivity {

    private String fName, profUrl;
    private static String TAG = "settingsMain.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_main);

        TextView profile = findViewById(R.id.settings_profile);
        TextView basicInfo = findViewById(R.id.settings_basic);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        //preData
        firebaseFirestore.collection("Users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid()).get().addOnCompleteListener(task -> {
            if (Objects.requireNonNull(task.getResult()).exists()) {
                fName = task.getResult().getString("name");
                profUrl = task.getResult().getString("thumb_id");
                Log.e(TAG, "onComplete: settingsMain: " + fName + " " + profUrl);

            }
        });

        profile.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsMain.this, PostRegisterFirst.class);
            intent.putExtra("name", fName);
            intent.putExtra("profUrl", profUrl);
            startActivity(intent);
        });

        basicInfo.setOnClickListener(v -> startActivity(new Intent(SettingsMain.this, PostRegisterSecond.class)));
    }
}
