package com.bcabuddies.fitsteps;


import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterSecondFrag extends Fragment {

    private TextInputEditText fullName;
    private Button btnNext;
    private CircleImageView thumbImage;
    String name,userId;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;

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
        auth=FirebaseAuth.getInstance();
        userId=auth.getCurrentUser().getUid();
        Log.e("userid", "onCreateView: "+ userId);
        firebaseFirestore=FirebaseFirestore.getInstance();
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterThirdFrag registerThirdFrag=new RegisterThirdFrag();
                getFragmentManager().beginTransaction().replace(R.id.registerMain_frame,registerThirdFrag).commit();
            }
        });

        return view;
    }

}
