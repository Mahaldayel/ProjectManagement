package com.example.hanan.projectmanagement.Task;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hanan.projectmanagement.DateDialogUtility;
import com.example.hanan.projectmanagement.Project.ProjectInformation;
import com.example.hanan.projectmanagement.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.hanan.projectmanagement.Project.ProjectViewAdapter.PROJECT_NAME_INTENT;
import static com.example.hanan.projectmanagement.Task.ViewTasksActivity.TASK_NAME_INTENT;

public class EditTaskActivity extends AppCompatActivity  implements View.OnClickListener{

    private EditText mTaskName_et;
    private EditText mStartDate_et;
    private EditText mEndDate_et;
    private EditText mTaskCost_et;
    private EditText mTaskResourse;
    private Button mEditTask;

    private FirebaseDatabase database;
    private DatabaseReference mTaskRef;

    private ArrayList<Task> mTaskArrayList;
    private ProjectTask mProjectTask;
    private ArrayList<ProjectTask> mProjectTaskArrayList;
    private String mProjectName;
    private String mTaskName;
    private int mClickedTaskIndex;

    private ProgressDialog progressDialog;
    private DatabaseReference mProjectRef;
    private ArrayList<ProjectInformation> mProjectInformationArrayList;
    private Task mCurrentTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        initElements();
        getProjectNameFromIntent();
        getTasksFromFirebase();
        getProjectsFromFirebase();
    }

    private void getProjectNameFromIntent() {

        Intent intent = getIntent();

        if(intent.hasExtra(PROJECT_NAME_INTENT))
            mProjectName = intent.getStringExtra(PROJECT_NAME_INTENT);
        if(intent.hasExtra(TASK_NAME_INTENT))
            mTaskName = intent.getStringExtra(TASK_NAME_INTENT);
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

        mEditTask = findViewById(R.id.edit);
        mEditTask.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");

    }

    private void setDataOnEditText(Task task) {


        mTaskName_et.setText(task.getName());
        mStartDate_et.setText(task.getStartDate());
        mEndDate_et.setText(task.getEndDate());

        showPickerDate(mStartDate_et,task.getStartDate());
        showPickerDate(mEndDate_et,task.getEndDate());

        progressDialog.dismiss();
    }


    private void getClickedTask(){


        for (ProjectTask projectTask : mProjectTaskArrayList){

            if(projectTask.getProjectName().equals(mProjectName)){
                mProjectTask = projectTask;
                for(Task task : mProjectTask.getTaskArrayList()){
                    if(task.getName().equals(mTaskName)){
                        mClickedTaskIndex = mProjectTask.getTaskArrayList().indexOf(task);
                        mCurrentTask = task;
                        setDataOnEditText(task);
                    }
                }
            }
        }

    }

    private Task getDataFromEditText(){

        mCurrentTask.setName(mTaskName_et.getText().toString());
        mCurrentTask.setStartDate( mStartDate_et.getText().toString());
        mCurrentTask.setEndDate(mEndDate_et.getText().toString());

        return mCurrentTask;
    }

    private void showPickerDate(final EditText editText, final String dateStr) {

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View view, boolean hasfocus){
                if(hasfocus){
                    String[] dateInt = dateStr.split("/");

                    DateDialogUtility dialog= new DateDialogUtility(view,Integer.parseInt(dateInt[2]),Integer.parseInt(dateInt[1])-1,Integer.parseInt(dateInt[0]));
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
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

            case R.id.edit:
                if(checkOfEmity()&&checkOfDate())
                    EditTaskToFirebase();
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
            Toast.makeText(EditTaskActivity.this,"The end date can not be before start date",Toast.LENGTH_LONG).show();
            return false;
        }


        return true;
    }


    private void EditTaskToFirebase() {

        EditTask();

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

    private void EditTask(){

        mProjectTask.getTaskArrayList().set(mClickedTaskIndex,getDataFromEditText());
        mProjectTaskArrayList.set(mProjectTaskArrayList.indexOf(mProjectTask),mProjectTask);

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

                    getClickedTask();

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

        if(TextUtils.isEmpty(mTaskName_et.getText().toString())){
            //email is empty
            Toast.makeText(EditTaskActivity.this,"Please enter task name",Toast.LENGTH_LONG).show();
            return false;
        }

        if(TextUtils.isEmpty(mStartDate_et.getText().toString())){
            //email is empty
            Toast.makeText(EditTaskActivity.this,"Please enter start date",Toast.LENGTH_LONG).show();
            return false;
        }

        if(TextUtils.isEmpty(mEndDate_et.getText().toString())){
            //email is empty
            Toast.makeText(EditTaskActivity.this,"Please enter end date",Toast.LENGTH_LONG).show();
            return false;
        }

        if(checkTaskNameAvailability()){
            Toast.makeText(EditTaskActivity.this,"Please enter anther task name",Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    public boolean checkTaskNameAvailability(){

        int count = 0;

        for(ProjectTask projectTask : mProjectTaskArrayList)
            if(projectTask.getProjectName().equals(mProjectName))
                for(Task task : projectTask.getTaskArrayList())
                    if(task.getName().equals(mTaskName_et.getText().toString())){
                        count ++;
                    }

        if(count == 2)
           return true;//available

        return false; // not available

    }

}

