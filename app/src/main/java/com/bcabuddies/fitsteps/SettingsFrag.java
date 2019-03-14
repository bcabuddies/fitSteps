package com.bcabuddies.fitsteps;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFrag extends Fragment {

    private TextView profile, basicInfo;
    private RegisterSecondFrag registerSecondFrag;
    private RegisterThirdFrag registerThirdFrag;

    public SettingsFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        profile = view.findViewById(R.id.settings_profile);
        basicInfo = view.findViewById(R.id.settings_basic);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerSecondFrag = new RegisterSecondFrag();
                getFragmentManager().beginTransaction().replace(R.id.settings_frame, registerSecondFrag).addToBackStack(null).commit();
            }
        });

        basicInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerThirdFrag=new RegisterThirdFrag();
                getFragmentManager().beginTransaction().replace(R.id.settings_frame,registerThirdFrag).addToBackStack(null).commit();
            }
        });


        return view;
    }

}
