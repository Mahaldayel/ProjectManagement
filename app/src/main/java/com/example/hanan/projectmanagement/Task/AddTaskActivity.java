package com.example.hanan.projectmanagement.Task;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hanan.projectmanagement.DateDialogUtility;
import com.example.hanan.projectmanagement.Project.ProjectInformation;
import com.example.hanan.projectmanagement.R;
import com.example.hanan.projectmanagement.Recourse.ProjectResource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.hanan.projectmanagement.Project.ProjectViewAdapter.PROJECT_NAME_INTENT;

public class AddTaskActivity extends AppCompatActivity  implements View.OnClickListener{

    private EditText mTaskName_et;
    private EditText mStartDate_et;
    private EditText mEndDate_et;
    private Button mAddTask;

    private FirebaseDatabase database;
    private DatabaseReference mTaskRef;

    private ArrayList<Task> mTaskArrayList;
    private ProjectTask mProjectTask;
    private ArrayList<ProjectTask> mProjectTaskArrayList;
    private String mProjectName;
    private ArrayList<ProjectInformation> mProjectInformationArrayList;
    private DatabaseReference mProjectRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initElements();
        getProjectNameFromIntent();
        getTasksFromFirebase();
        getProjectsFromFirebase();
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
        mProjectInformationArrayList = new ArrayList<>();
        mProjectTask = null;

        initUIElements();
        initFirebase();
    }

    private void initUIElements() {

        mTaskName_et = findViewById(R.id.task_name);
        mStartDate_et = findViewById(R.id.start_date);
        mEndDate_et = findViewById(R.id.end_date);

        mAddTask = findViewById(R.id.add);
        mAddTask.setOnClickListener(this);

        showPickerDate(mStartDate_et);
        showPickerDate(mEndDate_et);


    }

    private void showPickerDate(EditText editText) {

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View view, boolean hasfocus){
                if(hasfocus){
                    DateDialogUtility dialog= new DateDialogUtility(view);
                    FragmentTransaction ft =getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");

                }
            }

        });
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
                if(checkOfEmity()&&checkOfDate())
                    addTaskToFirebase();
                break;
        }
    }

    private boolean checkOfDate()  {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
        Date startDate = null;
        Date endDate = null;

        try {
            startDate = sdf.parse(mStartDate_et.getText().toString());
            endDate = sdf.parse(mEndDate_et.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(startDate == null || endDate == null)
            return false;

        if (startDate.after(endDate)){
            Toast.makeText(AddTaskActivity.this,"The end date can not be before start date",Toast.LENGTH_LONG).show();
            return false;
        }


        return true;
    }

    private void addTaskToFirebase() {

        addTask();

        updateTotalCostForCurrent(mProjectTaskArrayList);

        mProjectRef.setValue(mProjectInformationArrayList);
        mTaskRef.setValue(mProjectTaskArrayList);

        goTo(ViewTasksActivity.class);

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

        return new Task(mTaskName_et.getText().toString(), mStartDate_et.getText().toString(), mEndDate_et.getText().toString());

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

    private void getProjectsFromFirebase(){



        mProjectRef.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(final DataSnapshot snapshot) {
                if (snapshot.exists() ){


                    for (DataSnapshot dataValues : snapshot.getChildren()){
                        ProjectInformation projectInformation = dataValues.getValue(ProjectInformation.class);
                        if(projectInformation != null)
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

        if(TextUtils.isEmpty(mTaskName_et.getText().toString())){
            //email is empty
            Toast.makeText(AddTaskActivity.this,"Please enter task name",Toast.LENGTH_LONG).show();
            return false;
        }

        if(TextUtils.isEmpty(mStartDate_et.getText().toString())){
            //email is empty
            Toast.makeText(AddTaskActivity.this,"Please enter start date",Toast.LENGTH_LONG).show();
            return false;
        }

        if(TextUtils.isEmpty(mEndDate_et.getText().toString())){
            //email is empty
            Toast.makeText(AddTaskActivity.this,"Please enter end date",Toast.LENGTH_LONG).show();
            return false;
        }

        if(checkProjectNameAvailability()){
            Toast.makeText(AddTaskActivity.this,"Please enter anther task name",Toast.LENGTH_LONG).show();
            return false;
        }



        return true;
    }


    public boolean checkProjectNameAvailability(){


        for(ProjectTask projectTask : mProjectTaskArrayList)
            if(projectTask.getProjectName().equals(mProjectName))
                for(Task task : projectTask.getTaskArrayList())
                    if(task.getName().equals(mTaskName_et.getText().toString()))
                        return true;//available

        return false; // not available

    }
}

