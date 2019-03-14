package com.bcabuddies.fitsteps;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsMain extends AppCompatActivity {

    private TextView profile, basicInfo;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    String fName, profUrl;
    private static String TAG = "settingsmain.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_main);

        profile = findViewById(R.id.settings_profile);
        basicInfo = findViewById(R.id.settings_basic);
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //preData
        firebaseFirestore.collection("Users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    fName = task.getResult().getString("name");
                    profUrl = task.getResult().getString("thumb_id");
                    Log.e(TAG, "onComplete: settingsmain: "+fName+" "+profUrl );

                }
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsMain.this, PostRegisterFirst.class);
                intent.putExtra("name", fName);
                intent.putExtra("profUrl", profUrl);
                startActivity(intent);
            }
        });

        basicInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startActivity(new Intent(SettingsMain.this,PostRegisterSecond.class));
            }
        });


    }
}
