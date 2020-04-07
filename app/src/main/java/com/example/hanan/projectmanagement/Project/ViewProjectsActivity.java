package com.example.hanan.projectmanagement.Project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.preference.EditTextPreference;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.hanan.projectmanagement.R;
import com.example.hanan.projectmanagement.Task.ViewTasksActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.hanan.projectmanagement.Project.ProjectViewAdapter.PROJECT_NAME_INTENT;

public class ViewProjectsActivity extends AppCompatActivity implements View.OnClickListener ,AdapterView.OnItemClickListener{

    private ListView mViewProject;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ArrayList<ProjectInformation> mProjectInformationArrayList;
    private ProgressDialog progressDialog;
    private ProjectViewAdapter adpter;

    private FloatingActionButton mAdd;
    private String mProjectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_projects);

        initElements();
        getProjectsFromFirebase();
    }


    private void initElements(){

        mViewProject = findViewById(R.id.view_project);
        mViewProject.setOnItemClickListener(this);

        mProjectInformationArrayList = new ArrayList<>();

        mAdd = findViewById(R.id.add);
        mAdd.setOnClickListener(this);

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
                        mProjectInformationArrayList.add(projectInformation);

                    }

                    initAdapter();


                }else {
                    progressDialog.dismiss();


                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initAdapter() {


        adpter = new ProjectViewAdapter(this,R.layout.project_item_view, mProjectInformationArrayList);
        mViewProject.setAdapter(adpter);
        progressDialog.dismiss();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.add:
                goTo(AddProjectActivity.class);
                break;
            case R.id.projects:
                goTo(ViewProjectsActivity.class);
                break;

        }
    }

    private void goTo(Class nextClass) {

        Context context = this;
        Intent intent = new Intent(context,nextClass);

        intent.putExtra(PROJECT_NAME_INTENT,mProjectName);

        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        mProjectName = mProjectInformationArrayList.get(i).getName();
        goTo(ViewTasksActivity.class);

    }


}
