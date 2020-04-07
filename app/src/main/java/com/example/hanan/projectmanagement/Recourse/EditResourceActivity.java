package com.example.hanan.projectmanagement.Recourse;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
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
import static com.example.hanan.projectmanagement.Recourse.ResourceViewAdapter.RESOURCE_NAME_INTENT;
import static com.example.hanan.projectmanagement.Task.ViewTasksActivity.TASK_NAME_INTENT;

public class EditResourceActivity extends AppCompatActivity  implements View.OnClickListener{

    private EditText mResourceName_et;
    private Spinner mResourceType_spinner;
    private EditText mHourCost_et;
    private EditText mNumberOfHours_et;
    private Button mEditResource;

    private FirebaseDatabase database;
    private DatabaseReference mTaskRef;

    private ArrayList<Task> mTaskArrayList;
    private ProjectTask mProjectTask;
    private ArrayList<ProjectTask> mProjectTaskArrayList;
    private Task mCurrentTask;
    private String mProjectName;
    private String mTaskName;
    private int mResourceTypeIndex;
    private ArrayList<ProjectResource> mResourceArrayList;
    private String mResourceName;
    private ProjectResource mResource;
    private ProgressDialog progressDialog;
    private int mSelectResourceIndex;
    private DatabaseReference mProjectRef;
    private ArrayList<ProjectInformation> mProjectInformationArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_resource);

        initElements();
        getProjectNameFromIntent();
        getTaskNameFromIntent();
        getResourceNameFromIntent();
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

    private void getResourceNameFromIntent() {

        Intent intent = getIntent();

        if(intent.hasExtra(RESOURCE_NAME_INTENT))
            mResourceName = intent.getStringExtra(RESOURCE_NAME_INTENT);
    }

    private void initElements() {


        mTaskArrayList = new ArrayList<>();
        mProjectTask = new ProjectTask();
        mProjectTaskArrayList = new ArrayList<>();
        mProjectInformationArrayList = new ArrayList<>();
        mResourceArrayList = new ArrayList<>();
        mProjectTask = null;

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");

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


        mEditResource = findViewById(R.id.edit);
        mEditResource.setOnClickListener(this);


    }

    private void setDataOnEditText(ProjectResource projectResource) {

        setResourceType(projectResource.getType().toString());

        mResourceName_et.setText(projectResource.getName());
        mResourceType_spinner.setSelection(mResourceTypeIndex);
        mHourCost_et.setText(String.valueOf(projectResource.getHourCost()));
        mNumberOfHours_et.setText(String.valueOf(projectResource.getNumberOfhours()));



        progressDialog.dismiss();

    }


    private void getClickedResource(){


        for (ProjectTask projectTask : mProjectTaskArrayList){

            if(projectTask.getProjectName().equals(mProjectName)){
                mProjectTask = projectTask;
                for(Task task : mProjectTask.getTaskArrayList()){
                    if(task.getName().equals(mTaskName)){
                        mCurrentTask = task;
                       for(ProjectResource projectResource : task.getResourceArrayList()){
                            if(projectResource.getName().equals(mResourceName)){
                                setDataOnEditText(projectResource);
                                mSelectResourceIndex = task.getResourceArrayList().indexOf(projectResource);
                                mResource = projectResource;
                            }
                       }
                    }
                }
            }
        }

    }

    private void initFirebase() {

        database = FirebaseDatabase.getInstance();
        mTaskRef = database.getReference("Task");
        mProjectRef = database.getReference("Projects");


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.edit:
                if(checkOfEmity())
                    addTaskToFirebase();
                break;
        }
    }

    private void addTaskToFirebase() {

        EditResource();
        mTaskRef.setValue(mProjectTaskArrayList);

        goTo(ViewTasksActivity.class);

    }


    private void goTo(Class nextClass) {


        Intent intent = new Intent(this,nextClass);
        intent.putExtra(PROJECT_NAME_INTENT,mProjectName);
        startActivity(intent);
    }

    private void EditResource(){

        mResourceArrayList = mCurrentTask.getResourceArrayList();
        mResourceArrayList.set(mResourceArrayList.indexOf(mResource),createResourceObject());

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


    private ProjectResource createResourceObject() {

        return new ProjectResource(mResourceName_et.getText().toString(),mResourceType_spinner.getSelectedItem().toString(), Double.parseDouble(mHourCost_et.getText().toString()),Integer.parseInt(mNumberOfHours_et.getText().toString()));


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

        progressDialog.show();

        mTaskRef.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(final DataSnapshot snapshot) {
                if (snapshot.exists() ){


                    for (DataSnapshot dataValues : snapshot.getChildren()){
                        ProjectTask projectTask = dataValues.getValue(ProjectTask.class);
                        mProjectTaskArrayList.add(projectTask);

                    }

                    getClickedResource();
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
            Toast.makeText(EditResourceActivity.this,"Please enter resource name",Toast.LENGTH_LONG).show();
            return false;
        }

        if(TextUtils.isEmpty(mResourceType_spinner.getSelectedItem().toString())){
            //email is empty
            Toast.makeText(EditResourceActivity.this,"Please enter resource type",Toast.LENGTH_LONG).show();
            return false;
        }

        if(TextUtils.isEmpty(mHourCost_et.getText().toString())){
            //email is empty
            Toast.makeText(EditResourceActivity.this,"Please enter hour cost",Toast.LENGTH_LONG).show();
            return false;
        }

        if(TextUtils.isEmpty(mNumberOfHours_et.getText().toString())){
            //email is empty
            Toast.makeText(EditResourceActivity.this,"Please enter number of hours",Toast.LENGTH_LONG).show();
            return false;
        }


        if(checkResourceNameAvailability()){
            Toast.makeText(EditResourceActivity.this,"Please enter anther resource name",Toast.LENGTH_LONG).show();
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

    private void setResourceType(String resourceType){


        String[] resource_type_array = getResources().getStringArray(R.array.resource_type_array);

        int count = 0;
        for(String s : resource_type_array){

            if(s.equals(resourceType))
                mResourceTypeIndex = count;

            count++;
        }


    }
}

