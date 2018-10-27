package com.bcabuddies.fitsteps;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterSecondFrag extends Fragment {

    private TextInputEditText fullName;
    private Button btnNext;
    private CircleImageView thumbImage;
    private String name, userId;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private Context context;

    public RegisterSecondFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_second, container, false);

        btnNext = view.findViewById(R.id.register2_btnNext);
        thumbImage = view.findViewById(R.id.register2_profile);
        fullName = view.findViewById(R.id.register2_fullName);
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        context = getContext();
        Log.e("userid", "onCreateView: " + userId);
        firebaseFirestore = FirebaseFirestore.getInstance();
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = fullName.getText().toString();

                if (name.isEmpty()) {
                    Toast.makeText(context, "Please Enter your name ", Toast.LENGTH_SHORT).show();
                } else {

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("name", name);

                    firebaseFirestore.collection("Users").document(userId).update(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        RegisterThirdFrag registerThirdFrag = new RegisterThirdFrag();
                                        getFragmentManager().beginTransaction().replace(R.id.registerMain_frame, registerThirdFrag).commit();
                                    } else {
                                        Toast.makeText(context, "some error " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }
            }
        });

        return view;
    }

}
