package com.bcabuddies.fitsteps;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Button runBtn;
    private ImageView toogleNavigation;
    private DrawerLayout sideDrawerLayout;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private String fullName, thumbUrl, userId;
    private CircleImageView thumb_image;
    private TextView name;
    private NavigationView navigationView;
    private ArrayList<HomeData> homeDataList;
    private RecyclerView recyclerView;
    private HomeRecyclerAdapter homeRecyclerAdapter;
    private String TAG = "Home.java";


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
        navigationView = findViewById(R.id.homeNav_view);
        navigationView.setNavigationItemSelectedListener(this);

        homeDataList = new ArrayList<>();
        homeRecyclerAdapter = new HomeRecyclerAdapter(homeDataList);
        recyclerView = findViewById(R.id.home_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(homeRecyclerAdapter);


        runBtn.setOnClickListener(v -> startActivity(new Intent(Home.this,Steps.class)));

        //side nav bar
        toogleNavigation.setOnClickListener(v -> sideDrawerLayout.openDrawer(Gravity.LEFT));

        //loading thumb image and full name in side navbar
        firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(task -> {
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
        }).addOnFailureListener(e -> Toast.makeText(Home.this, "Some error has occured", Toast.LENGTH_SHORT).show());


        //home recyclerview data
        Query firstQuery = firebaseFirestore.collection("RunData")
                .orderBy("time", Query.Direction.DESCENDING).whereEqualTo("uid",auth.getCurrentUser().getUid().toString());
        try {
            firstQuery.addSnapshotListener(Home.this, (queryDocumentSnapshots, e) -> {
                try {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (final DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {

                                String homePostId = documentChange.getDocument().getId();
                                Log.e("recyclertest", "homepostid: " + homePostId);
                                final HomeData homeData = documentChange.getDocument().toObject(HomeData.class).withID(homePostId);
                                homeDataList.add(homeData);

                                Log.e("recyclertest", "onEvent: homedatalist " + homeDataList);
                                Log.e("recyclertest", "onEvent: homedata " + homeData);
                                homeRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Log.e("mytest1", "onEvent: no data ");
                        Toast.makeText(Home.this, "No data", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                    Log.e("mytest2", "onEvent: exception " + e1.getMessage());
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("mytest3", "onCreate: Exception with recycler data " + e.getMessage());
        }


    }

    //side navigagtion items clicked
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(Home.this,SettingsMain.class));
                break;
            case R.id.menu_logout:
                auth.signOut();
                startActivity(new Intent(Home.this, Welcome.class));
                this.finish();
                break;

        }

        return true;
    }
}
