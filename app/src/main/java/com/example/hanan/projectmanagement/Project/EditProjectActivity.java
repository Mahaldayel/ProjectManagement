package com.example.hanan.projectmanagement.Project;

import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hanan.projectmanagement.DateDialogUtility;
import com.example.hanan.projectmanagement.R;
import com.example.hanan.projectmanagement.Task.EditTaskActivity;
import com.example.hanan.projectmanagement.Task.ProjectTask;
import com.example.hanan.projectmanagement.Task.Task;
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

public class EditProjectActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText mProjectName_et;
    private EditText mStartDate_et;
    private EditText mEndDate_et;

    private Button mEdit_bt;
    private FirebaseDatabase database;
    private DatabaseReference mProjectRef;

    private DatePickerDialog.OnDateSetListener mDateSetListener;


    private ArrayList<ProjectInformation> mProjectInformationArrayList;
    private String mProjectName;
    private int mClickedProjectIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_project);

        initElements();
        getProjectsFromFirebase();
        getProjectNameFromIntent();
//        getClickedProject();
    }

    private void getProjectNameFromIntent() {

        Intent intent = getIntent();

        if(intent.hasExtra(PROJECT_NAME_INTENT))
            mProjectName = intent.getStringExtra(PROJECT_NAME_INTENT);
    }


    private void getClickedProject(){

        for(ProjectInformation projectInformation: mProjectInformationArrayList){

            if(projectInformation.getName().equals(mProjectName)){
                mClickedProjectIndex = mProjectInformationArrayList.indexOf(projectInformation);
                setDataOnEditText(projectInformation);

            }
        }

    }

    private void setDataOnEditText(ProjectInformation projectInformation) {

        mProjectName_et.setText(projectInformation.getName());
        mEndDate_et.setText(projectInformation.getEndDate());
        mStartDate_et.setText(projectInformation.getStartDate());

        showPickerDate(mStartDate_et,projectInformation.getStartDate());
        showPickerDate(mEndDate_et,projectInformation.getEndDate());
    }

    private void initElements(){

        mProjectName_et = findViewById(R.id.project_name);
        mStartDate_et = findViewById(R.id.start_date);
        mEndDate_et = findViewById(R.id.end_date);


        mEdit_bt = findViewById(R.id.edit_bt);
        mEdit_bt.setOnClickListener(this);

        mProjectInformationArrayList = new ArrayList<>();

        initFirebase();


    }


    private void showPickerDate(final EditText editText, final String dateStr) {

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View view, boolean hasfocus){
                if(hasfocus){
                    String[] dateInt = dateStr.split("/");

                    DateDialogUtility dialog= new DateDialogUtility(view,Integer.parseInt(dateInt[2]),Integer.parseInt(dateInt[1])-1,Integer.parseInt(dateInt[0]));
                    FragmentTransaction ft =getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");


                }
            }

        });
    }

    private void initFirebase() {

         database = FirebaseDatabase.getInstance();
         mProjectRef = database.getReference("Projects");

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.edit_bt:
                if(checkOfEmity()&&checkOfDate())
                    editProject();
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
            Toast.makeText(EditProjectActivity.this,"The end date can not be before start date",Toast.LENGTH_LONG).show();
            return false;
        }


        return true;
    }

    private ProjectInformation getDataFromEditText(){

        ProjectInformation projectInformation = new ProjectInformation(mProjectName_et.getText().toString(),mStartDate_et.getText().toString(),mEndDate_et.getText().toString());
        projectInformation.setTotalCost(mProjectInformationArrayList.get(mClickedProjectIndex).getTotalCost());
        return projectInformation;
    }

    private void editProject() {

        mProjectInformationArrayList.set(mClickedProjectIndex,getDataFromEditText());

        mProjectRef.setValue(mProjectInformationArrayList);

        goTo(ViewProjectsActivity.class);
    }

    private void goTo(Class nextClass) {

        Context context = this;
        Intent intent = new Intent(context,nextClass);
        startActivity(intent);
    }


    private void getProjectsFromFirebase(){


        mProjectRef.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(final DataSnapshot snapshot) {
                if (snapshot.exists() ){


                    for (DataSnapshot dataValues : snapshot.getChildren()){
                        ProjectInformation projectInformation = dataValues.getValue(ProjectInformation.class);
                        mProjectInformationArrayList.add(projectInformation);

                    }

                    getClickedProject();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private boolean checkOfEmity(){

        if(TextUtils.isEmpty(mProjectName_et.getText().toString())){
            //email is empty
            Toast.makeText(EditProjectActivity.this,"Please enter project name",Toast.LENGTH_LONG).show();
            return false;
        }

        if(TextUtils.isEmpty(mStartDate_et.getText().toString())){
            //email is empty
            Toast.makeText(EditProjectActivity.this,"Please enter start date",Toast.LENGTH_LONG).show();
            return false;
        }

        if(TextUtils.isEmpty(mEndDate_et.getText().toString())){
            //email is empty
            Toast.makeText(EditProjectActivity.this,"Please enter end date",Toast.LENGTH_LONG).show();
            return false;
        }


        if(checkProjectNameAvailability()){
            Toast.makeText(EditProjectActivity.this,"Please enter anther project name",Toast.LENGTH_LONG).show();
            return false;
        }



        return true;
    }



    public boolean checkProjectNameAvailability(){

        int count = 0;

        for(ProjectInformation projectInformation : mProjectInformationArrayList)
            if(projectInformation.getName().equals(mProjectName_et.getText().toString())){
                count ++;

            }

        if(count == 2)
            return true;//available

        return false; // not available

    }


}
