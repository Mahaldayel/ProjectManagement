package com.example.hanan.projectmanagement.Project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.projectmanagement.R;
import com.example.hanan.projectmanagement.Task.ProjectTask;
import com.example.hanan.projectmanagement.Task.ViewTasksActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProjectViewAdapter extends ArrayAdapter implements View.OnClickListener{


    public static final String PROJECT_NAME_INTENT = "PROJECT_NAME_INTENT";

    private final Context mContext;
    private final ArrayList<ProjectInformation> mProjects;
    private final LayoutInflater mLayoutInflater;
    private final int mViewResourceId;

    private TextView mProjectName;
    private TextView mStartDate;
    private TextView mEndDate;
    private TextView mProjectCost;

    private Button mDeleteBt;
    private Button mEditBt;
    private Button mViewBt;

    private FirebaseDatabase database;
    private DatabaseReference mProjectRef;
    private ArrayList<ProjectInformation> mProjectInformationArrayList;
    private ProgressDialog progressDialog;
    private int mSelectedProjectIndex;
    private DatabaseReference mTaskRef;
    private ArrayList<ProjectTask> mProjectTaskArrayList;


    public ProjectViewAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ProjectInformation> projects) {
        super(context, resource, projects);

        mContext = context;
        this.mProjects = projects;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = resource;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(mViewResourceId, null);

        if(convertView != null) {
            initElements(convertView);

            mProjectName.setText(mProjects.get(position).getName());
            mStartDate.setText(mProjects.get(position).getStartDate());
            mEndDate.setText(mProjects.get(position).getEndDate());
            mProjectCost.setText(String.valueOf(mProjects.get(position).getTotalCost()));

            mDeleteBt.setTag(position);
            mEditBt.setTag(position);
            mViewBt.setTag(position);

        }


        return convertView;
    }



    private void initElements(View convertView) {

        mProjectName = convertView.findViewById(R.id.project_name);
        mStartDate = convertView.findViewById(R.id.start_date);
        mEndDate = convertView.findViewById(R.id.end_date);
        mProjectCost = convertView.findViewById(R.id.project_cost);

        mDeleteBt = convertView.findViewById(R.id.delete);
        mDeleteBt.setOnClickListener(this);

        mEditBt = convertView.findViewById(R.id.edit);
        mEditBt.setOnClickListener(this);

        mViewBt = convertView.findViewById(R.id.view);
        mViewBt.setOnClickListener(this);

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Deleting ...");

        mProjectInformationArrayList = new ArrayList<>();
        mProjectTaskArrayList = new ArrayList<>();

        initFirebaseElements();

    }

    private void initFirebaseElements() {

        database = FirebaseDatabase.getInstance();
        mProjectRef = database.getReference("Projects");
        mTaskRef = database.getReference("Task");

    }




    @Override
    public void onClick(View view) {

        mSelectedProjectIndex = (int) view.getTag();

        switch (view.getId()){
            case R.id.delete:
                getProjectsFromFirebase();
                break;
            case R.id.edit:
                goTo(EditProjectActivity.class);
                break;
            case R.id.view:
                goTo(ViewTasksActivity.class);
                break;

        }
    }

    private void goTo(Class nextClass) {

        Intent intent = new Intent(mContext,nextClass);

        intent.putExtra(PROJECT_NAME_INTENT,mProjects.get(mSelectedProjectIndex).getName());

        mContext.startActivity(intent);
    }


    private void deleteProject() {

        deleteProjectTask();

        mProjectInformationArrayList.remove(mSelectedProjectIndex);
        mProjectRef.setValue(mProjectInformationArrayList);
        Toast.makeText(mContext,"Has been deleted successfully",Toast.LENGTH_LONG);
        progressDialog.dismiss();
        goTo(ViewProjectsActivity.class);

    }

    private void deleteProjectTask(){

        for(ProjectTask projectTask : mProjectTaskArrayList){
            if(projectTask.getProjectName().equals(mProjectInformationArrayList.get(mSelectedProjectIndex).getName()))
                mProjectTaskArrayList.remove(mProjectTaskArrayList.indexOf(projectTask));

        }

        mTaskRef.setValue(mProjectTaskArrayList);
    }

    private void getProjectsFromFirebase(){

        progressDialog.show();

        mProjectRef.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(final DataSnapshot snapshot) {
                if (snapshot.exists() ){


                    for (DataSnapshot dataValues : snapshot.getChildren()){
                        ProjectInformation projectInformation = dataValues.getValue(ProjectInformation.class);
                        mProjectInformationArrayList.add(projectInformation);
                        }

                    getTasksFromFirebase();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getTasksFromFirebase(){

        mTaskRef.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(final DataSnapshot snapshot) {
                if (snapshot.exists() ){


                    for (DataSnapshot dataValues : snapshot.getChildren()){
                        ProjectTask projectTask = dataValues.getValue(ProjectTask.class);
                        mProjectTaskArrayList.add(projectTask);

                    }

                    deleteProject();


                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
