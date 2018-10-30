package com.bcabuddies.fitsteps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Home extends AppCompatActivity {

    private Button runBtn;
    private ImageView toogleNavigation;
    private DrawerLayout sideDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        runBtn = findViewById(R.id.home_toolbar_runBtn);
        sideDrawerLayout=findViewById(R.id.home_drawerLayout);
        toogleNavigation=findViewById(R.id.home_toolbar_menuBtn);

        toogleNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sideDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });


        runBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, Run.class);
                startActivity(i);
            }
        });
    }
}
