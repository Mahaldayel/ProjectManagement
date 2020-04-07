package com.example.hanan.projectmanagement.Task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.hanan.projectmanagement.Project.ViewProjectsActivity;
import com.example.hanan.projectmanagement.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.hanan.projectmanagement.Project.ProjectViewAdapter.PROJECT_NAME_INTENT;

public class ViewTasksActivity extends AppCompatActivity implements View.OnClickListener  {


    public static final String TASK_NAME_INTENT = "TASK_NAME_INTENT";


    private ListView mViewTask;
    private FirebaseDatabase database;
    private DatabaseReference mTaskRef;
    private ArrayList<Task> mTaskArrayList;
    private ProgressDialog progressDialog;
    private TaskViewAdapter adapter;
    private ProjectTask mProjectTask;
    private String mProjectName;
    private ArrayList<ProjectTask> mProjectTaskArrayList;
    private FloatingActionButton mAdd;
    private Button mProjects_bt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tasks);

        initElements();
        getTasksFromFirebase();
        getProjectNameFromIntent();

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
        adapter.setProjectName(mProjectName);
        progressDialog.dismiss();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.add:
                goTo(AddTaskActivity.class);
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



}
