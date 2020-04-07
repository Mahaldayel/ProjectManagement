package com.example.hanan.projectmanagement.Project;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.hanan.projectmanagement.Project.ProjectInformation;
import com.example.hanan.projectmanagement.R;
import com.example.hanan.projectmanagement.Task.AddTaskActivity;
import com.example.hanan.projectmanagement.Task.Task;
import com.example.hanan.projectmanagement.Task.ViewTasksActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProjectViewAdapter extends ArrayAdapter{


    public static final String PROJECT_NAME_INTENT = "PROJECT_NAME_INTENT";

    private final Context mContext;
    private final ArrayList<ProjectInformation> mProjects;
    private final LayoutInflater mLayoutInflater;
    private final int mViewResourceId;

    private TextView mProjectName;
    private TextView mStartDate;
    private TextView mEndDate;
    private TextView mTotalCost;

    private Button mAddTask;
    private Button mViewTask;

    private int mCurrentPosition;



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
            ProjectInformation projectInformation = new ProjectInformation();
            Double numbers = projectInformation.getTotalCost();
            String cost = new Double(numbers).toString();
           mTotalCost.setText(cost);
            addOnClickListanerForAddTaskButton(position);
            addOnClickListanerFoViewTaskButton(position);
           // mCurrentPosition = position;
        }
            return convertView;
        }

    private void addOnClickListanerFoViewTaskButton(final int position) {

        mViewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentPosition = position;
                goTo(ViewTasksActivity.class);

            }
        });

    }

    private void addOnClickListanerForAddTaskButton(final int position) {

        mAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentPosition = position;
                goTo(AddTaskActivity.class);

            }
        });
    }

    private void initElements(View convertView) {

        mProjectName = convertView.findViewById(R.id.project_name);
        mStartDate = convertView.findViewById(R.id.start_date);
        mEndDate = convertView.findViewById(R.id.end_date);

        mAddTask = convertView.findViewById(R.id.add_task);
        mViewTask = convertView.findViewById(R.id.view_task);
        mTotalCost =convertView.findViewById(R.id.total_cost);
    }



    private void goTo(Class nextClass) {


        Intent intent = new Intent(mContext,nextClass);
        intent.putExtra(PROJECT_NAME_INTENT,mProjects.get(mCurrentPosition).getName());
        mContext.startActivity(intent);
    }


}
