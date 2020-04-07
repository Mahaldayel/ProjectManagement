package com.example.hanan.projectmanagement.Recourse;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.hanan.projectmanagement.Project.ViewProjectsActivity;
import com.example.hanan.projectmanagement.R;
import com.example.hanan.projectmanagement.Task.AddTaskActivity;
import com.example.hanan.projectmanagement.Task.ProjectTask;
import com.example.hanan.projectmanagement.Task.Task;
import com.example.hanan.projectmanagement.Task.TaskViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.hanan.projectmanagement.Project.ProjectViewAdapter.PROJECT_NAME_INTENT;
import static com.example.hanan.projectmanagement.Task.ViewTasksActivity.TASK_NAME_INTENT;

public class ViewResourcesActivity extends AppCompatActivity implements View.OnClickListener  {


    public static final String TASK_NAME_INTENT = "TASK_NAME_INTENT";



    private ListView mViewResource;
    private FirebaseDatabase database;
    private DatabaseReference mTaskRef;
    private ArrayList<Task> mTaskArrayList;
    private ProgressDialog progressDialog;
    private ResourceViewAdapter adapter;
    private ProjectTask mProjectTask;
    private String mProjectName;
    private ArrayList<ProjectTask> mProjectTaskArrayList;
    private FloatingActionButton mAdd;
    private String mTaskName;
    private Task mCurrentTask;
    private Button mProjects_bt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_resources);

        initElements();
        getTasksFromFirebase();
        getTaskNameFromIntent();
        getProjectNameFromIntent();

    }

    private void getProjectNameFromIntent() {

        Intent intent = getIntent();

        if(intent.hasExtra(PROJECT_NAME_INTENT))
            mProjectName = intent.getStringExtra(PROJECT_NAME_INTENT);
    }

    private void getTaskNameFromIntent() {

        Intent intent = getIntent();

        if(intent.hasExtra(TASK_NAME_INTENT))
            mTaskName = intent.getStringExtra(TASK_NAME_INTENT);
    }

    private void initElements(){

        mViewResource = findViewById(R.id.view_resource);

        mTaskArrayList = new ArrayList<>();
        mProjectTaskArrayList = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");

        mAdd = findViewById(R.id.add);
        mAdd.setOnClickListener(this);

        mProjects_bt = findViewById(R.id.projects);
        mProjects_bt.setOnClickListener(this);

        initFirebaseElements();
    }

    private void initFirebaseElements() {

        database = FirebaseDatabase.getInstance();
        mTaskRef = database.getReference("Task");
    }



    private void getTasksFromFirebase(){

        progressDialog.show();

        mTaskRef.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(final DataSnapshot snapshot) {
                if (snapshot.exists() ){


                    for (DataSnapshot dataValues : snapshot.getChildren()){
                        ProjectTask projectTask = dataValues.getValue(ProjectTask.class);
                        mProjectTaskArrayList.add(projectTask);

                    }

                    setCurrentTask();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setCurrentTask(){

        for(ProjectTask projectTask : mProjectTaskArrayList){

            if(projectTask.getProjectName().equals(mProjectName)){
                mTaskArrayList = projectTask.getTaskArrayList();
                for(Task task : mTaskArrayList){
                    if(task.getName().equals(mTaskName)){
                        mCurrentTask = task;
                    }
                }
            }
        }
        initAdapter();

    }

    private void initAdapter() {

        ArrayList<ProjectResource> projectResourceArrayList = mCurrentTask.getResourceArrayList();
        if(projectResourceArrayList == null)
            projectResourceArrayList = new ArrayList<>();


        adapter = new ResourceViewAdapter(this, R.layout.resource_item_view, projectResourceArrayList);
        mViewResource.setAdapter(adapter);
        adapter.setProjectName(mProjectName);
        adapter.setTaskName(mTaskName);
        progressDialog.dismiss();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.add:
                goTo(AddResourceActivity.class);
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
        intent.putExtra(TASK_NAME_INTENT,mTaskName);


        startActivity(intent);
    }



}
