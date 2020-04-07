package com.example.hanan.projectmanagement.Task;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.hanan.projectmanagement.Project.ProjectInformation;
import com.example.hanan.projectmanagement.Project.ProjectViewAdapter;
import com.example.hanan.projectmanagement.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.hanan.projectmanagement.Project.ProjectViewAdapter.PROJECT_NAME_INTENT;

public class ViewTasksActivity extends AppCompatActivity {


    private ListView mViewTask;
    private FirebaseDatabase database;
    private DatabaseReference mTaskRef;
    private ArrayList<Task> mTaskArrayList;
    private ProgressDialog progressDialog;
    private TaskViewAdapter adapter;
    private ProjectTask mProjectTask;
    private String mProjectName;
    private ArrayList<ProjectTask> mProjectTaskArrayList;
    public double projectCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tasks);

        initElements();
        getProjectNameFromIntent();
        getTasksFromFirebase();


    }

    private void getProjectNameFromIntent() {

        Intent intent = getIntent();

        if(intent.hasExtra(PROJECT_NAME_INTENT))
            mProjectName = intent.getStringExtra(PROJECT_NAME_INTENT);
    }


    private void initElements(){

        mViewTask = findViewById(R.id.view_task);

        mTaskArrayList = new ArrayList<>();
        mProjectTaskArrayList = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");

        projectCost = 0;

        initFirebaseElements();
    }

    private void initFirebaseElements() {

        database = FirebaseDatabase.getInstance();
        mTaskRef = database.getReference("Task");
    }



    private void getTasksFromFirebase(){

        mTaskRef.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(final DataSnapshot snapshot) {
                if (snapshot.exists() ){


                    for (DataSnapshot dataValues : snapshot.getChildren()){
                        ProjectTask projectTask = dataValues.getValue(ProjectTask.class);

                        mProjectTaskArrayList.add(projectTask);

                    }

                    setmTaskArrayList();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setmTaskArrayList(){

        for(ProjectTask projectTask : mProjectTaskArrayList){

            if(projectTask.getProjectName().equals(mProjectName)){
                mTaskArrayList = projectTask.getTaskArrayList();
            }
        }
        initAdapter();

    }

    private void initAdapter() {

        adapter = new TaskViewAdapter(this,R.layout.task_item_view, mTaskArrayList);
        mViewTask.setAdapter(adapter);
        progressDialog.dismiss();
    }



}
