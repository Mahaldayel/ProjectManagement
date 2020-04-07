package com.example.hanan.projectmanagement;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import com.example.hanan.projectmanagement.Project.AddProjectActivity;
import com.example.hanan.projectmanagement.Project.ViewProjectsActivity;

public class MainActivity extends AppCompatActivity {


//    private Button mViewProjects_bt;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        inintElements();
//    }
//
//    private void inintElements() {
//
//        mViewProjects_bt = findViewById(R.id.view_project_bt);
//        mViewProjects_bt.setOnClickListener(this);
//
//    }
//
//    @Override
//    public void onClick(View view) {
//
//        switch (view.getId()){
//            case R.id.view_project_bt:
//                goTo(ViewProjectsActivity.class);
//                break;
//        }
//    }
//
//    private void goTo(Class nextClass) {
//
//        Context context = this;
//        Intent intent = new Intent(context,nextClass);
//        startActivity(intent);
//    }


    private final int SPLASH_DISPLAY_LENGTH = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create logo animation
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.blink);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(2000);

        final ImageView splash = (ImageView) findViewById(R.id.logo);
        splash.startAnimation(animation);
        //make the activity fullscreen
        hideSystemUI();

        // Start home activity
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-JoinActivity. */
                Intent loginActivity = new Intent(MainActivity.this,ViewProjectsActivity.class);
                MainActivity.this.startActivity(loginActivity);

                MainActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    public void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }




}
