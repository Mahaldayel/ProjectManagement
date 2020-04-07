package com.example.hanan.projectmanagement.Project;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.hanan.projectmanagement.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddProjectActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText mProjectName_et;
    private EditText mStartDate_et;
    private EditText mEndDate_et;

    private Button mAdd_bt;
    private FirebaseDatabase database;
    private DatabaseReference mProjectRef;
    private double cost=0.0;

    private ArrayList<ProjectInformation> mProjectInformationArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        initElements();
        getProjectsFromFirebase();


    }

    private void initElements(){

        mProjectName_et = findViewById(R.id.project_name);
        mStartDate_et = findViewById(R.id.start_date);
        mEndDate_et = findViewById(R.id.end_date);

        mAdd_bt = findViewById(R.id.add);
        mAdd_bt.setOnClickListener(this);

        mProjectInformationArrayList = new ArrayList<>();

        initFirebase();
    }

    private void initFirebase() {

         database = FirebaseDatabase.getInstance();
         mProjectRef = database.getReference("Projects");

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.add:
                addProject();
                break;
        }
    }

    private void addProject() {

        if(mProjectInformationArrayList != null)
            mProjectInformationArrayList.add((ProjectInformation) createObject());

        mProjectRef.setValue(mProjectInformationArrayList);

        goTo(ViewProjectsActivity.class);
    }

    private void goTo(Class nextClass) {

        Context context = this;
        Intent intent = new Intent(context,nextClass);
        startActivity(intent);
    }

    private Object createObject() {

        return new ProjectInformation(mProjectName_et.getText().toString(),mStartDate_et.getText().toString(),mEndDate_et.getText().toString() );
    }


    private void getProjectsFromFirebase(){



        mProjectRef.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(final DataSnapshot snapshot) {
                if (snapshot.exists() ){


                    for (DataSnapshot dataValues : snapshot.getChildren()){
                        ProjectInformation projectInformation = dataValues.getValue(ProjectInformation.class);
                        mProjectInformationArrayList.add(projectInformation);

                    }

                }else {

                    mProjectInformationArrayList.add((ProjectInformation) createObject());
                    mProjectRef.setValue(mProjectInformationArrayList);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
