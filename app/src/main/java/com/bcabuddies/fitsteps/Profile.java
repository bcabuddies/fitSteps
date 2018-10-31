package com.bcabuddies.fitsteps;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private String TAG = "Profile.java";
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String current_user;
    private RecyclerView recyclerView;
    private CircleImageView circleImageView;
    private TextView nameTV;
    private ArrayList<HomeData> profileDataList;
    private HomeRecyclerAdapter profileRecyclerAdapter;
    private TextView totalRunTV, achievTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        current_user = user.getUid();

        profileDataList = new ArrayList<>();
        profileRecyclerAdapter = new HomeRecyclerAdapter(profileDataList);

        recyclerView = findViewById(R.id.profile_recyclerView);
        circleImageView = findViewById(R.id.profile_profile_imageView);
        nameTV = findViewById(R.id.profile_name_TV);
        totalRunTV = findViewById(R.id.profile_runTV);
        achievTV = findViewById(R.id.profile_achievTV);

        recyclerView.setLayoutManager(new LinearLayoutManager(Profile.this));
        recyclerView.setAdapter(profileRecyclerAdapter);

        //getting name and profile pic
        firestore.collection("Users").document(current_user).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {
                            Log.e(TAG, "onComplete: exists ");
                            String name = task.getResult().getString("name");
                            String thumb_image = task.getResult().getString("thumb_id");

                            Log.e(TAG, "onComplete: name " + name);
                            Log.e(TAG, "onComplete: thumb image url " + thumb_image);

                            nameTV.setText(name);

                            try {
                                Glide.with(Profile.this).load(thumb_image).into(circleImageView);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG, "onComplete: exception with image " + e.getMessage());
                            }

                        } else {
                            Log.e(TAG, "onComplete: error " + task.getException().getMessage());
                        }
                    }
                });

        //getting run data
        Query q = firestore.collection("RunData").orderBy("time", Query.Direction.DESCENDING).whereEqualTo("uid", current_user);
        try {
            q.addSnapshotListener(Profile.this, new EventListener<QuerySnapshot>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                    try {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    String id = doc.getDocument().getId();
                                    Log.e(TAG, "onEvent: id " + id);
                                    HomeData homeList = doc.getDocument().toObject(HomeData.class).withID(id);
                                    profileDataList.add(homeList);
                                    Log.e(TAG, "onEvent: profile data " + profileDataList);
                                    profileRecyclerAdapter.notifyDataSetChanged();
                                    totalRunTV.setText(getResources().getString(R.string.total_run) + String.valueOf(profileDataList.size()));
                                }
                            }
                        } else {
                            Log.e(TAG, "onEvent: no data ");
                            Toast.makeText(Profile.this, "No data", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        Log.e(TAG, "onEvent: exception " + e1.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "onCreate: Exception with recycler data " + e.getMessage());
        }
    }
}
