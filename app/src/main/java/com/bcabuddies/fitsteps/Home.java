package com.bcabuddies.fitsteps;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity {

    private Button runBtn;
    private ImageView toogleNavigation;
    private DrawerLayout sideDrawerLayout;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private String fullName, thumbUrl, userId;
    private CircleImageView thumb_image;
    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        runBtn = findViewById(R.id.home_toolbar_runBtn);
        sideDrawerLayout = findViewById(R.id.home_drawerLayout);
        toogleNavigation = findViewById(R.id.home_toolbar_menuBtn);
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = auth.getCurrentUser().getUid();


        //side nav bar
        toogleNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sideDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });


        //start run button
        runBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, MapsActivity.class);
                startActivity(i);
            }
        });


        //loading thumb image and full name in side navbar
        firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    thumbUrl = task.getResult().getString("thumb_id");
                    fullName = task.getResult().getString("name");
                    // name.setText(fullName);
                    thumb_image = findViewById(R.id.homeNav_thumbImage);
                    name = findViewById(R.id.homeNav_name);


                    Log.e("dpname", "onComplete: thumb: " + thumbUrl + "\n name: " + fullName);
                    try {
                        Log.e("dpname", "try");
                        name.setText(fullName);
                        Glide.with(getApplicationContext()).load(thumbUrl).into(thumb_image);
                    } catch (Exception e) {
                        Log.e("dpname", "catch");
                    }
                } else {
                    Toast.makeText(Home.this, "user not exist", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Home.this, "Some error has occured", Toast.LENGTH_SHORT).show();
            }
        });

    }


}
