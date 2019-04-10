package com.bcabuddies.fitsteps;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFrag extends Fragment {

    private ArrayList<HomeData> homeDataList;
    private HomeRecyclerAdapter homeRecyclerAdapter;

    public ProfileFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        homeDataList = new ArrayList<>();
        homeRecyclerAdapter = new HomeRecyclerAdapter(homeDataList);
        RecyclerView recyclerView = view.findViewById(R.id.profile_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(homeRecyclerAdapter);

        //home recyclerView data
        Query firstQuery = firebaseFirestore.collection("RunData")
                .orderBy("time", Query.Direction.DESCENDING).whereEqualTo("uid", Objects.requireNonNull(auth.getCurrentUser()).getUid());
        try {
            firstQuery.addSnapshotListener(Objects.requireNonNull(getActivity()), (queryDocumentSnapshots, e) -> {
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

    static Fragment newInstance() {
        return new ProfileFrag();
    }

}
