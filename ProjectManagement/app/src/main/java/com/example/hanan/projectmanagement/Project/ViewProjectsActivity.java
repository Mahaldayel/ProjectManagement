package com.example.hanan.projectmanagement.Project;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.hanan.projectmanagement.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewProjectsActivity extends AppCompatActivity  {

    private ListView mViewProject;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ArrayList<ProjectInformation> projectInformationArrayList;
    private ProgressDialog progressDialog;
    private ProjectViewAdapter adpter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_projects);

        initElements();
        getProjectsFromFirebase();
    }


    private void initElements(){

        mViewProject = findViewById(R.id.view_project);

        projectInformationArrayList = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");

        initFirebaseElements();
    }

    private void initFirebaseElements() {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Projects");
    }



    private void getProjectsFromFirebase(){

        progressDialog.show();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(final DataSnapshot snapshot) {
                if (snapshot.exists() ){


                    for (DataSnapshot dataValues : snapshot.getChildren()){
                        ProjectInformation projectInformation = dataValues.getValue(ProjectInformation.class);
                        projectInformationArrayList.add(projectInformation);

                        initAdapter();
                    }


                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initAdapter() {


        adpter = new ProjectViewAdapter(this,R.layout.project_item_view,projectInformationArrayList);
        mViewProject.setAdapter(adpter);
        progressDialog.dismiss();
    }


}
