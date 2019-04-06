package com.bcabuddies.fitsteps;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bcabuddies.fitsteps.HomeData;
import com.bcabuddies.fitsteps.HomeRecyclerAdapter;
import com.bcabuddies.fitsteps.R;
import com.bcabuddies.fitsteps.SettingsMain;
import com.bcabuddies.fitsteps.Welcome;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFrag extends Fragment {

    //private DrawerLayout sideDrawerLayout;
    private FirebaseAuth auth;
    private String fullName;
    private String thumbUrl;
    private CircleImageView thumb_image;
    private TextView name;
    private ArrayList<HomeData> homeDataList;
    private HomeRecyclerAdapter homeRecyclerAdapter;
    private String TAG = "Home.java";


    public ProfileFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


      //  sideDrawerLayout = view.findViewById(R.id.profile_drawer);
        ImageView toggleNavigation = view.findViewById(R.id.home_toolbar_menuBtn);
        auth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        NavigationView navigationView = view.findViewById(R.id.profileNav_view);




        homeDataList = new ArrayList<>();
        homeRecyclerAdapter = new HomeRecyclerAdapter(homeDataList);
        RecyclerView recyclerView = view.findViewById(R.id.profile_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(homeRecyclerAdapter);

        //side nav bar
       // toggleNavigation.setOnClickListener(v -> sideDrawerLayout.openDrawer(Gravity.LEFT));


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_settings:
                        startActivity(new Intent(getActivity(), SettingsMain.class));
                        break;
                    case R.id.menu_logout:
                        auth.signOut();
                        startActivity(new Intent(getActivity(), Welcome.class));
                        getActivity().finish();
                        break;

                }
                return true;
            }
        });

        //loading thumb image and full name in side navbar
        firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(task -> {
            if (Objects.requireNonNull(task.getResult()).exists()) {

                thumbUrl = task.getResult().getString("thumb_id");
                fullName = task.getResult().getString("name");
                // name.setText(fullName);
                thumb_image = view.findViewById(R.id.homeNav_thumbImage);
                name = view.findViewById(R.id.homeNav_name);
                Log.e(TAG, "onComplete: thumb: " + thumbUrl + "\n name: " + fullName);
                try {
                    Log.e(TAG, "try");
                    name.setText(fullName);
                    Glide.with(getActivity()).load(thumbUrl).into(thumb_image);
                } catch (Exception e) {
                    Log.e("dpName", "catch");
                }
            } else {
                Toast.makeText(getActivity(), "user not exist", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Some error has occured", Toast.LENGTH_SHORT).show());


        //home recyclerView data
        Query firstQuery = firebaseFirestore.collection("RunData")
                .orderBy("time", Query.Direction.DESCENDING).whereEqualTo("uid", auth.getCurrentUser().getUid());
        try {
            firstQuery.addSnapshotListener(getActivity(), (queryDocumentSnapshots, e) -> {
                try {
                    assert queryDocumentSnapshots != null;
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (final DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {

                                String homePostId = documentChange.getDocument().getId();
                                Log.e("recyclertest", "homepostid: " + homePostId);
                                final HomeData homeData = documentChange.getDocument().toObject(HomeData.class).withID(homePostId);
                                homeDataList.add(homeData);

                                Log.e("recyclertest", "onEvent: homeDataList " + homeDataList);
                                Log.e("recyclertest", "onEvent: homeData " + homeData);
                                homeRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Log.e("myTest1", "onEvent: no data ");
                        Toast.makeText(getActivity(), "No data", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                    Log.e("myTest2", "onEvent: exception " + e1.getMessage());
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("myTest3", "onCreate: Exception with recycler data " + e.getMessage());
        }


        return view;
    }
    public static Fragment newInstance() {
        ProfileFrag fragment = new ProfileFrag();
        return fragment;

    }

}
