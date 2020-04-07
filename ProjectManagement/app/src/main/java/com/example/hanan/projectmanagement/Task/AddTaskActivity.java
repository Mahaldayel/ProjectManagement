package com.example.hanan.projectmanagement.Task;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.hanan.projectmanagement.MainActivity;
import com.example.hanan.projectmanagement.Project.ProjectInformation;
import com.example.hanan.projectmanagement.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.AbstractList;
import java.util.ArrayList;

import static com.example.hanan.projectmanagement.Project.ProjectViewAdapter.PROJECT_NAME_INTENT;

public class AddTaskActivity extends AppCompatActivity  implements View.OnClickListener{

    private EditText mTaskName;
    private EditText mStartDate;
    private EditText mEndDate;
    private EditText mTaskCost;
    private EditText mTaskResourse;
    private Button mAddTask;

    private FirebaseDatabase database;
    private DatabaseReference mTaskRef;
    private DatabaseReference mProjectRef;

    private ArrayList<Task> mTaskArrayList;
    private ProjectTask mProjectTask;
    private ArrayList<ProjectTask> mProjectTaskArrayList;
    private String mProjectName;
    private Double projectCost=0.0;
    private ProjectInformation mCurrentProject;
    private ArrayList<ProjectInformation> mProjectArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initElements();
        getProjectNameFromIntent();
        getTasksFromFirebase();
        getProjectsFromFirebase();
        sumCost();
    }

    private void getProjectNameFromIntent() {

        Intent intent = getIntent();

        if(intent.hasExtra(PROJECT_NAME_INTENT))
            mProjectName = intent.getStringExtra(PROJECT_NAME_INTENT);
    }

    private void initElements() {


        mTaskArrayList = new ArrayList<>();
        mProjectTask = new ProjectTask();
        mProjectTaskArrayList = new ArrayList<>();
        mProjectArrayList = new ArrayList<>();
        mProjectTask = null;

        initUIElements();
        initFirebase();

    }

    private void initUIElements() {

        mTaskName = findViewById(R.id.task_name);
        mTaskCost = findViewById(R.id.cost);
        mTaskResourse = findViewById(R.id.resource);
        mStartDate = findViewById(R.id.start_date);
        mEndDate = findViewById(R.id.end_date);

        mAddTask = findViewById(R.id.add);
        mAddTask.setOnClickListener(this);

    }


    private void initFirebase() {

        database = FirebaseDatabase.getInstance();
        mTaskRef = database.getReference("Task");
        mProjectRef = database.getReference("Projects");

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.add:
                addTaskToFirebase();
                break;
        }
    }

    private void addTaskToFirebase() {

        addTask();
        mTaskRef.setValue(mProjectTaskArrayList);
        sumCost();

        goTo(ViewTasksActivity.class);

    }


    private void goTo(Class nextClass) {


        Intent intent = new Intent(this,nextClass);
        intent.putExtra(PROJECT_NAME_INTENT,mProjectName);
        startActivity(intent);
    }

    private void addTask(){

        for (ProjectTask projectTask : mProjectTaskArrayList){

            if(projectTask.getProjectName().equals(mProjectName)){
                mProjectTask = projectTask;
                mProjectTask.getTaskArrayList().add(createTaskObject());
            }
        }

        if(mProjectTask == null){
            mProjectTask = createProjectTaskObject();
            mProjectTaskArrayList.add(mProjectTask);
        }
    }

    private ProjectTask createProjectTaskObject(){

        mTaskArrayList.add(createTaskObject());
        return new ProjectTask(mTaskArrayList,mProjectName);

    }

    private Task createTaskObject() {

        return new Task(mTaskName.getText().toString(),mStartDate.getText().toString(),mEndDate.getText().toString(),Double.parseDouble(mTaskCost.getText().toString()));
    }

    private void getTasksFromFirebase(){

        mTaskRef.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(final DataSnapshot snapshot) {
                if (snapshot.exists() ){


                    for (DataSnapshot dataValues : snapshot.getChildren()){
                        ProjectTask projectTask = dataValues.getValue(ProjectTask.class);
                        mProjectTaskArrayList.add(projectTask);

                    }


                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void sumCost(){

        for(ProjectTask projectTask: mProjectTaskArrayList){
            if(projectTask.getProjectName().equals(mProjectName)){
                for(Task task: projectTask.getTaskArrayList()){
                    double taskCost = task.getCost();
                    projectCost+= taskCost;
//                    ProjectInformation projectInfo = new ProjectInformation();
                    mCurrentProject.setTotalCost(2000.0);
//                    projectInfo.setTotalCost(projectCost);



                }}


        }
        mProjectRef.setValue(mProjectArrayList);
        }
    private void getProjectsFromFirebase(){


                mProjectRef.addListenerForSingleValueEvent(new ValueEventListener() {

                    public void onDataChange(final DataSnapshot snapshot) {
                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Projects");

                        if (snapshot.exists() ){


                            for (DataSnapshot dataValues : snapshot.getChildren()){
                                ProjectInformation projectInformation = dataValues.getValue(ProjectInformation.class);
                               mProjectArrayList.add(projectInformation);                            }

                            }

                        setCurrentProject();
                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




        }

        private void setCurrentProject(){

            for(ProjectInformation projectInformation : mProjectArrayList)
                if(projectInformation.getName().equals(mProjectName))
                    mCurrentProject = projectInformation;

        }
}


