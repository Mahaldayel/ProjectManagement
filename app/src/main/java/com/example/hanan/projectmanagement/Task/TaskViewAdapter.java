package com.example.hanan.projectmanagement.Task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
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
import com.example.hanan.projectmanagement.Recourse.ViewResourcesActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.hanan.projectmanagement.Project.ProjectViewAdapter.PROJECT_NAME_INTENT;
import static com.example.hanan.projectmanagement.Task.ViewTasksActivity.TASK_NAME_INTENT;

public class TaskViewAdapter extends ArrayAdapter implements View.OnClickListener{

    private final Context mContext;
    private final ArrayList<Task> mTasks;
    private final LayoutInflater mLayoutInflater;
    private final int mViewResourceId;

    private TextView mTaskName;
    private TextView mStartDate;
    private TextView mEndDate;
    private TextView mCost;
    private ProgressDialog progressDialog;
    private ArrayList<ProjectTask> mProjectTaskArrayList;
    private FirebaseDatabase database;
    private DatabaseReference mTaskRef;
    private int mSelectedProjectIndex;
    private Button mDeleteBt;
    private Button mEditBt;
    private Button mAddResourceBt;

    private String mProjectName;

    private ProjectTask mProjectTask;
    private ArrayList<ProjectInformation> mProjectInformationArrayList;
    private DatabaseReference mProjectRef;
    private Button mCalendarBt;

    private boolean isDelete;


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


            mDeleteBt.setTag(position);
            mEditBt.setTag(position);
            mAddResourceBt.setTag(position);
            mCalendarBt.setTag(position);


        }


        return convertView;
    }

    private void initElements(View convertView) {

        mTaskName = convertView.findViewById(R.id.task_name);
        mStartDate = convertView.findViewById(R.id.start_date);
        mEndDate = convertView.findViewById(R.id.end_date);
        mCost = convertView.findViewById(R.id.task_cost);


        mDeleteBt = convertView.findViewById(R.id.delete);
        mDeleteBt.setOnClickListener(this);

        mEditBt = convertView.findViewById(R.id.edit);
        mEditBt.setOnClickListener(this);

        mCalendarBt = convertView.findViewById(R.id.calendar);
        mCalendarBt.setOnClickListener(this);

        mAddResourceBt = convertView.findViewById(R.id.viewResource);
        mAddResourceBt.setOnClickListener(this);


        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Deleting ...");


        mProjectInformationArrayList = new ArrayList<>();

        mProjectTaskArrayList = new ArrayList<>();

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
                        if(projectTask != null)
                            mProjectTaskArrayList.add(projectTask);

                    }

                    setProjectTask();
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

        mSelectedProjectIndex = (int) view.getTag();

        switch (view.getId()){
            case R.id.delete:
                getTasksFromFirebase();
                isDelete = true;
                break;
            case R.id.edit:
                goTo(EditTaskActivity.class);
                break;
            case R.id.viewResource:
                goTo(ViewResourcesActivity.class);
                isDelete = false;
                break;
            case R.id.calendar:
                goToCalendarContract();
                isDelete = false;
                break;

        }
    }

    private void goTo(Class nextClass) {

        Intent intent = new Intent(mContext,nextClass);

        intent.putExtra(PROJECT_NAME_INTENT,mProjectName);
        if(!isDelete)
            intent.putExtra(TASK_NAME_INTENT,mTasks.get(mSelectedProjectIndex).getName());


        mContext.startActivity(intent);
    }

    private void setProjectTask(){

        if(mProjectTaskArrayList == null)
            return;

        for (ProjectTask projectTask : mProjectTaskArrayList){

            if(projectTask.getProjectName().equals(mProjectName))
                mProjectTask = projectTask;
        }
    }


    private void deleteTask() {

        mTasks.remove(mSelectedProjectIndex);
        mProjectTask.setTaskArrayList(mTasks);

        if(mTasks.size() == 0)
            mProjectTaskArrayList.remove(mProjectTaskArrayList.indexOf(mProjectTask));
        else
            mProjectTaskArrayList.set(mProjectTaskArrayList.indexOf(mProjectTask),mProjectTask);

        updateTotalCostForCurrent(mProjectTaskArrayList);

        mProjectRef.setValue(mProjectInformationArrayList);
        mTaskRef.setValue(mProjectTaskArrayList);

        Toast.makeText(mContext,"Has been deleted successfully",Toast.LENGTH_LONG);
        progressDialog.dismiss();

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


    /**/
    private void goToCalendarContract(){


        String startDate =  mTasks.get(mSelectedProjectIndex).getStartDate().toString();
        int startDateDay= Integer.parseInt(startDate.substring(0,startDate.indexOf("/")));
        int startDateMonth= Integer.parseInt(startDate.substring(startDate.indexOf("/")+1,startDate.indexOf("/",3)));
        int startDateYear= Integer.parseInt(startDate.substring(startDate.indexOf("/",3)+1));

        String endDate =  mTasks.get(mSelectedProjectIndex).getEndDate().toString();
        int endDateDay= Integer.parseInt(endDate.substring(0,endDate.indexOf("/")));
        int endDateMonth= Integer.parseInt(endDate.substring(endDate.indexOf("/")+1,endDate.indexOf("/",3)));
        int endDateYear= Integer.parseInt(endDate.substring(endDate.indexOf("/",3)+1));


        Calendar beginTime = Calendar.getInstance();
        beginTime.set(startDateYear, startDateMonth-1, startDateDay,8,0);
        Calendar endTime = Calendar.getInstance();
        endTime.set(endDateYear, endDateMonth-1, endDateDay,5,0);
        Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                    .putExtra(CalendarContract.Events.TITLE, "Task")
                    .putExtra(CalendarContract.Events.DESCRIPTION, "Task name :"+mTasks.get(mSelectedProjectIndex).getName())
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        mContext.startActivity(intent);

    }
}
