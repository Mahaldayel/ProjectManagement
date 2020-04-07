package com.example.hanan.projectmanagement.Recourse;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.projectmanagement.Project.ProjectInformation;
import com.example.hanan.projectmanagement.Project.ViewProjectsActivity;
import com.example.hanan.projectmanagement.R;
import com.example.hanan.projectmanagement.Task.ProjectTask;
import com.example.hanan.projectmanagement.Task.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.hanan.projectmanagement.Project.ProjectViewAdapter.PROJECT_NAME_INTENT;
import static com.example.hanan.projectmanagement.Task.ViewTasksActivity.TASK_NAME_INTENT;

public class ResourceViewAdapter extends ArrayAdapter implements View.OnClickListener{

    public static final String RESOURCE_NAME_INTENT = "RESOURCE_NAME_INTENT";


    private final Context mContext;
    private final ArrayList<ProjectResource> mResources;
    private final LayoutInflater mLayoutInflater;
    private final int mViewResourceId;
    private TextView mResourceName;
    private TextView mResourceType;
    private TextView mHourCost;
    private TextView mNumberOfHours;
    private ProgressDialog progressDialog;
    private ArrayList<ProjectTask> mProjectTaskArrayList;
    private FirebaseDatabase database;
    private DatabaseReference mTaskRef;
    private int mSelectedResourceIndex;
    private Button mDeleteBt;
    private Button mEditBt;
    private Button mAddResourceBt;

    private String mProjectName;

    private ProjectTask mProjectTask;
    private String mTaskName;
    private Task mCurrentTask;
    private ArrayList<ProjectInformation> mProjectInformationArrayList;
    private DatabaseReference mProjectRef;
    private boolean isDelete;


    public ResourceViewAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ProjectResource> resources) {
        super(context, resource, resources);

        mContext = context;
        this.mResources = resources;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = resource;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(mViewResourceId, null);

        if(convertView != null && mResources != null) {
            initElements(convertView);

            mResourceName.setText(mResources.get(position).getName());
            mResourceType.setText(mResources.get(position).getType().toString());
            mHourCost.setText(String.valueOf(mResources.get(position).getHourCost()));
            mNumberOfHours.setText(String.valueOf(mResources.get(position).getNumberOfhours()));


            mDeleteBt.setTag(position);
            mEditBt.setTag(position);

        }


        return convertView;
    }

    private void initElements(View convertView) {

        mResourceName = convertView.findViewById(R.id.resource_name);
        mResourceType = convertView.findViewById(R.id.resource_type);
        mHourCost = convertView.findViewById(R.id.hour_cost);
        mNumberOfHours = convertView.findViewById(R.id.number_of_hours);



        mDeleteBt = convertView.findViewById(R.id.delete);
        mDeleteBt.setOnClickListener(this);

        mEditBt = convertView.findViewById(R.id.edit);
        mEditBt.setOnClickListener(this);

        mProjectTaskArrayList = new ArrayList<>();

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Deleting ...");

        mProjectInformationArrayList = new ArrayList<>();

        initFirebaseElements();


    }

    private void initFirebaseElements() {

        database = FirebaseDatabase.getInstance();
        mTaskRef = database.getReference("Task");
        mProjectRef = database.getReference("Projects");

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
                    getProjectsFromFirebase();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View view) {

        mSelectedResourceIndex = (int) view.getTag();

        switch (view.getId()){
            case R.id.delete:
                getTasksFromFirebase();
                isDelete = true;
                break;
            case R.id.edit:
                goTo(EditResourceActivity.class);
                isDelete = false;
                break;

        }
    }

    private void goTo(Class nextClass) {

        Intent intent = new Intent(mContext,nextClass);

        intent.putExtra(PROJECT_NAME_INTENT,mProjectName);
        intent.putExtra(TASK_NAME_INTENT,mTaskName);
        if(!isDelete)
            intent.putExtra(RESOURCE_NAME_INTENT,mResources.get(mSelectedResourceIndex).getName());

        mContext.startActivity(intent);
    }



    private void deleteTask() {

        mCurrentTask.getResourceArrayList().remove(mSelectedResourceIndex);


        mCurrentTask.setCost(calculateProjectResourceCost(mCurrentTask.getResourceArrayList()));

        updateTotalCostForCurrent(mProjectTaskArrayList);

        mProjectRef.setValue(mProjectInformationArrayList);
        mTaskRef.setValue(mProjectTaskArrayList);

        Toast.makeText(mContext,"Has been deleted successfully",Toast.LENGTH_LONG);
        progressDialog.dismiss();

        goTo(ViewResourcesActivity.class);

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

    private void setCurrentTask(){

        for(ProjectTask projectTask : mProjectTaskArrayList){
            if(projectTask.getProjectName().equals(mProjectName)){
                for(Task task : projectTask.getTaskArrayList()){
                    if(task.getName().equals(mTaskName)){
                        mCurrentTask = task;
                    }
                }
            }
        }
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



    private void getProjectsFromFirebase(){


        mProjectRef.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(final DataSnapshot snapshot) {
                if (snapshot.exists() ){


                    for (DataSnapshot dataValues : snapshot.getChildren()){
                        ProjectInformation projectInformation = dataValues.getValue(ProjectInformation.class);
                        mProjectInformationArrayList.add(projectInformation);

                    }

                    deleteTask();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void setProjectName(String projectName) {

        mProjectName = projectName;
    }

    public void setTaskName(String taskName) {

        mTaskName = taskName;
    }


}
