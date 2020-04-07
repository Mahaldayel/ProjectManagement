package com.example.hanan.projectmanagement.Recourse;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hanan.projectmanagement.Project.ProjectInformation;
import com.example.hanan.projectmanagement.R;
import com.example.hanan.projectmanagement.Task.ProjectTask;
import com.example.hanan.projectmanagement.Task.Task;
import com.example.hanan.projectmanagement.Task.ViewTasksActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.hanan.projectmanagement.Project.ProjectViewAdapter.PROJECT_NAME_INTENT;
import static com.example.hanan.projectmanagement.Task.ViewTasksActivity.TASK_NAME_INTENT;

public class AddResourceActivity extends AppCompatActivity  implements View.OnClickListener , AdapterView.OnItemSelectedListener {

    private EditText mResourceName_et;
    private Spinner mResourceType_spinner;
    private EditText mHourCost_et;
    private EditText mNumberOfHours_et;
    private Button mAddResource;

    private FirebaseDatabase database;
    private DatabaseReference mTaskRef;

    private ArrayList<Task> mTaskArrayList;
    private ProjectTask mProjectTask;
    private ArrayList<ProjectTask> mProjectTaskArrayList;
    private Task mCurrentTask;
    private String mProjectName;
    private String mTaskName;
    private String mResourceType;
    private ArrayList<ProjectResource> mResourceArrayList;
    private DatabaseReference mProjectRef;
    private ArrayList<ProjectInformation> mProjectInformationArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_resource);

        initElements();
        getProjectNameFromIntent();
        getTaskNameFromIntent();
        getTasksFromFirebase();
        getProjectsFromFirebase();
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

    private void initElements() {


        mTaskArrayList = new ArrayList<>();
        mProjectTask = new ProjectTask();
        mProjectInformationArrayList = new ArrayList<>();
        mProjectTaskArrayList = new ArrayList<>();
        mResourceArrayList = new ArrayList<>();
        mProjectTask = null;

        initUIElements();
        initFirebase();
    }

    private void initUIElements() {

        mResourceName_et = findViewById(R.id.resource_name);
        mNumberOfHours_et = findViewById(R.id.number_oF_hours);
        mHourCost_et = findViewById(R.id.hour_cost);

        mResourceType_spinner = findViewById(R.id.resource_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.resource_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mResourceType_spinner.setAdapter(adapter);

        mAddResource = findViewById(R.id.add);
        mAddResource.setOnClickListener(this);


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
                if(checkOfEmity())
                    addTaskToFirebase();
                break;
        }
    }

    private void addTaskToFirebase() {

        addResource();
        mTaskRef.setValue(mProjectTaskArrayList);

        goTo(ViewResourcesActivity.class);

    }


    private void goTo(Class nextClass) {


        Intent intent = new Intent(this,nextClass);
        intent.putExtra(PROJECT_NAME_INTENT,mProjectName);
        intent.putExtra(TASK_NAME_INTENT,mTaskName);
        startActivity(intent);
    }

    private void addResource(){

        mResourceArrayList = mCurrentTask.getResourceArrayList();

        if(mResourceArrayList == null){
            mResourceArrayList = createNewResourceArrayList();

        }else {
            mResourceArrayList.add(createResourceObject());
        }

        mCurrentTask.setResourceArrayList(mResourceArrayList);
        mCurrentTask.setCost(calculateProjectResourceCost(mResourceArrayList));

        updateTotalCostForCurrent(mProjectTaskArrayList);

        mTaskRef.setValue(mProjectTaskArrayList);
        mProjectRef.setValue(mProjectInformationArrayList);

    }


    private void updateTotalCostForCurrent(ArrayList<ProjectTask> projectTasks){

        double totalCost = calculateTotalCostForCurrentProject(projectTasks);

        for(ProjectInformation projectInformation : mProjectInformationArrayList){
            if(projectInformation.getName().equals(mProjectName)){
                projectInformation.setTotalCost(totalCost);
            }

        }
    }


    private double calculateTotalCostForCurrentProject(ArrayList<ProjectTask> projectTasks) {


        double cost = 0;

        if(projectTasks != null) {
            for (ProjectTask projectTask : projectTasks) {

                if(projectTask.getProjectName().equals(mProjectName)){
                    for(Task task : projectTask.getTaskArrayList())
                        cost += task.getCost();
                }
            }
        }


        return cost;
    }

    private ArrayList<ProjectResource> createNewResourceArrayList() {

        ArrayList<ProjectResource> projectResourceArrayList = new ArrayList<>();
        projectResourceArrayList.add(createResourceObject());

        return projectResourceArrayList;
    }


    private ProjectResource createResourceObject() {

        return new ProjectResource(mResourceName_et.getText().toString(),mResourceType_spinner.getSelectedItem().toString(), Double.parseDouble(mHourCost_et.getText().toString()),Integer.parseInt(mNumberOfHours_et.getText().toString()));


    }


    private void setCurrentTask(){


        for(ProjectTask projectTask : mProjectTaskArrayList){
            if(projectTask.getProjectName().equals(mProjectName)){
                mTaskArrayList = projectTask.getTaskArrayList();
                for(Task task: mTaskArrayList){
                    if(task.getName().equals(mTaskName)){
                        mCurrentTask = task;
                    }
                }
            }

        }
    }



    private double calculateProjectResourceCost(ArrayList<ProjectResource> resources){

        double cost = 0;

        if(resources != null) {
            for (ProjectResource projectResource : resources) {

                cost += projectResource.getHourCost() * projectResource.getNumberOfhours();
            }
        }

        return cost;

    }

    private void getTasksFromFirebase(){

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

    private void getProjectsFromFirebase(){



        mProjectRef.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(final DataSnapshot snapshot) {
                if (snapshot.exists() ){


                    for (DataSnapshot dataValues : snapshot.getChildren()){
                        ProjectInformation projectInformation = dataValues.getValue(ProjectInformation.class);
                        mProjectInformationArrayList.add(projectInformation);

                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private boolean checkOfEmity(){

        if(TextUtils.isEmpty(mResourceName_et.getText().toString())){
            //email is empty
            Toast.makeText(AddResourceActivity.this,"Please enter resource name",Toast.LENGTH_LONG).show();
            return false;
        }

        if(TextUtils.isEmpty(mResourceType_spinner.getSelectedItem().toString())){
            //email is empty
            Toast.makeText(AddResourceActivity.this,"Please enter resource type",Toast.LENGTH_LONG).show();
            return false;
        }

        if(TextUtils.isEmpty(mHourCost_et.getText().toString())){
            //email is empty
            Toast.makeText(AddResourceActivity.this,"Please enter hour cost",Toast.LENGTH_LONG).show();
            return false;
        }

        if(TextUtils.isEmpty(mNumberOfHours_et.getText().toString())){
            //email is empty
            Toast.makeText(AddResourceActivity.this,"Please enter number of hours",Toast.LENGTH_LONG).show();
            return false;
        }


        if(checkResourceNameAvailability()){
            Toast.makeText(AddResourceActivity.this,"Please enter anther resource name",Toast.LENGTH_LONG).show();
            return false;
        }



        return true;
    }


    public boolean checkResourceNameAvailability(){


        for(ProjectTask projectTask : mProjectTaskArrayList)
            if(projectTask.getProjectName().equals(mProjectName))
                for(Task task : projectTask.getTaskArrayList())
                    if(task.getName().equals(mResourceName_et.getText().toString()))
                        return true;//available

        return false; // not available

    }


    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}

