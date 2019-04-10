package com.bcabuddies.fitsteps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder> {

    @SuppressLint("StaticFieldLeak")
    public static Context context;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<HomeData> homeDataList;

    HomeRecyclerAdapter(ArrayList<HomeData> homeDataList) {
        this.homeDataList = homeDataList;
    }

    @NonNull
    @Override
    public HomeRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_row, viewGroup, false);
        context = viewGroup.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeRecyclerAdapter.ViewHolder viewHolder, int i) {

        final String homePostId = homeDataList.get(i).HomeDataId;
        final String homeDataUserId = homeDataList.get(i).getUid();

        //   final String full_name = homeDataList.get(i).getName();
        final String distance = homeDataList.get(i).getDistance();
        final String cal = homeDataList.get(i).getCalories();
        final String step = homeDataList.get(i).getSteps();
        Date date = homeDataList.get(i).getTime();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateformatMMDDYYYY = new SimpleDateFormat("dd MMM yy \t HH:mm");
        final StringBuilder nowMMDDYYYY = new StringBuilder(dateformatMMDDYYYY.format(date));

        firebaseFirestore.collection("Users").document(homeDataUserId).get().addOnCompleteListener(task -> {
            if (Objects.requireNonNull(task.getResult()).exists()) {
                String thumb_url = task.getResult().getString("thumb_id");
                String fullName = task.getResult().getString("name");
                viewHolder.setThumb_image(thumb_url);
                viewHolder.setName(fullName);
                viewHolder.setDistance(distance);
                viewHolder.setCalories(cal);
                viewHolder.setSteps(step);
                viewHolder.setTime_stamp(nowMMDDYYYY);
            }
        });
        Log.e("recyclertest", "homePostId on adapter: " + homePostId);
        Log.e("recyclertest", "homeDataUserId: " + homeDataUserId);
        Log.e("recyclertest", "fullName ");
        Log.e("recyclertest", "distance:  " + distance);

        setAnimation(viewHolder.itemView);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return homeDataList.size();
    }

    private void setAnimation(View itemView) {
        // If the bound view wasn't previously displayed on screen, it's animated
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            itemView.startAnimation(animation);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private CircleImageView thumb_image;
        private TextView name, distance, time_stamp, steps, calories;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            thumb_image = mView.findViewById(R.id.homerow_thumb);
            name = mView.findViewById(R.id.homerow_name);
            distance = mView.findViewById(R.id.homerow_distance);
            time_stamp = mView.findViewById(R.id.homerow_time);
            steps = mView.findViewById(R.id.homerow_steps);
            calories = mView.findViewById(R.id.homerow_cal);
        }


        void setThumb_image(String thumb_url) {
            Glide.with(context).load(thumb_url).into(thumb_image);
        }

        public void setName(String fname) {
            name.setText(fname);
        }

        @SuppressLint("SetTextI18n")
        void setDistance(String dist) {
            distance.setText("Distance: " + dist);
        }

        void setTime_stamp(StringBuilder time) {
            time_stamp.setText(time);
        }

        @SuppressLint("SetTextI18n")
        void setSteps(String step) {
            steps.setText("Steps: " + step);
        }

        @SuppressLint("SetTextI18n")
        void setCalories(String cal) {
            calories.setText("Calories: " + cal);
        }
    }

}
