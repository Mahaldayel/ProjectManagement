package com.example.hanan.projectmanagement;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.hanan.projectmanagement.Project.AddProjectActivity;
import com.example.hanan.projectmanagement.Project.ViewProjectsActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private Button mAddProject_bt;
    private Button mViewProjects_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inintElements();
    }

    private void inintElements() {

        mAddProject_bt = findViewById(R.id.add_project_bt);
        mAddProject_bt.setOnClickListener(this);

        mViewProjects_bt = findViewById(R.id.view_project_bt);
        mViewProjects_bt.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.add_project_bt:
                goTo(AddProjectActivity.class);
                break;
            case R.id.view_project_bt:
                goTo(ViewProjectsActivity.class);
                break;
        }
    }

    private void goTo(Class nextClass) {

        Context context = this;
        Intent intent = new Intent(context,nextClass);
        startActivity(intent);
    }
}
