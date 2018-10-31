package com.bcabuddies.fitsteps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder> {

    public static Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private String userId;
    private ArrayList<HomeData> homeDataList;

    public HomeRecyclerAdapter(ArrayList<HomeData> homeDataList) {
        this.homeDataList = homeDataList;
    }

    @NonNull
    @Override
    public HomeRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_row, viewGroup, false);
        context = viewGroup.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeRecyclerAdapter.ViewHolder viewHolder, int i) {

        final String homePostId = homeDataList.get(i).HomeDataId;
        final String homeDataUserId = homeDataList.get(i).getUid();

        final String full_name = homeDataList.get(i).getName();
        final String distance = homeDataList.get(i).getDistance();
        Date date = homeDataList.get(i).getTime();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateformatMMDDYYYY = new SimpleDateFormat("dd MMM yy \t HH:mm");
        final StringBuilder nowMMDDYYYY = new StringBuilder(dateformatMMDDYYYY.format(date));

        firebaseFirestore.collection("Users").document(homeDataUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    String thumb_url = task.getResult().getString("thumb_id");
                    viewHolder.setThumb_image(thumb_url);
                    viewHolder.setName(full_name);
                    viewHolder.setDistance(distance);

                    viewHolder.setTime_stamp(nowMMDDYYYY);

                }
            }
        });


        Log.e("recyclertest", "homepostid on adapter: " + homePostId);
        Log.e("recyclertest", "homedatauserid: " + homeDataUserId);
        Log.e("recyclertest", "fullname " + full_name);
        Log.e("recyclertest", "distance:  " + distance);


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return homeDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private CircleImageView thumb_image;
        private TextView name, distance, time_stamp;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            thumb_image = mView.findViewById(R.id.homerow_thumb);
            name = mView.findViewById(R.id.homerow_name);
            distance = mView.findViewById(R.id.homerow_distance);
            time_stamp = mView.findViewById(R.id.homerow_time);
        }


        public void setThumb_image(String thumb_url) {
            Glide.with(context).load(thumb_url).into(thumb_image);
        }

        public void setName(String fname) {
            name.setText(fname);
        }

        public void setDistance(String dist) {
            distance.setText(dist);
        }

        public void setTime_stamp(StringBuilder time) {
            time_stamp.setText(time);
        }
    }
}
