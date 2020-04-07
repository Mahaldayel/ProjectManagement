package com.example.hanan.projectmanagement.Task;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.hanan.projectmanagement.R;

import java.util.ArrayList;

public class TaskViewAdapter extends ArrayAdapter{

    private final Context mContext;
    private final ArrayList<Task> mTasks;
    private final LayoutInflater mLayoutInflater;
    private final int mViewResourceId;

    private TextView mTaskName;
    private TextView mStartDate;
    private TextView mEndDate;
    private TextView mCost;



    public TaskViewAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Task> tasks) {
        super(context, resource, tasks);

        mContext = context;
        this.mTasks = tasks;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = resource;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(mViewResourceId, null);

        if(convertView != null) {
            initElements(convertView);

            mTaskName.setText(mTasks.get(position).getName());
            mStartDate.setText(mTasks.get(position).getStartDate());
            mEndDate.setText(mTasks.get(position).getEndDate());
            mCost.setText(String.valueOf(mTasks.get(position).getCost()));

        }


        return convertView;
    }

    private void initElements(View convertView) {

        mTaskName = convertView.findViewById(R.id.task_name);
        mStartDate = convertView.findViewById(R.id.start_date);
        mEndDate = convertView.findViewById(R.id.end_date);
        mCost = convertView.findViewById(R.id.task_cost);


    }

}
