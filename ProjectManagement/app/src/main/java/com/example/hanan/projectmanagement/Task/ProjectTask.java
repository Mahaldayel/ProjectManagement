package com.example.hanan.projectmanagement.Task;

import java.util.ArrayList;

public class ProjectTask {

    private ArrayList<Task> taskArrayList;
    private String projectName;


    public ProjectTask() {
    }

    public ProjectTask(ArrayList<Task> taskArrayList, String projectName) {
        this.taskArrayList = taskArrayList;
        this.projectName = projectName;
    }


    public ArrayList<Task> getTaskArrayList() {
        return taskArrayList;
    }

    public void setTaskArrayList(ArrayList<Task> taskArrayList) {
        this.taskArrayList = taskArrayList;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
