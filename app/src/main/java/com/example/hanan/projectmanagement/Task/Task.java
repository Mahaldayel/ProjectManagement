package com.example.hanan.projectmanagement.Task;

import com.example.hanan.projectmanagement.Recourse.ProjectResource;

import java.util.ArrayList;

public class Task {


    private String name;
    private String startDate;
    private String endDate;
    private double cost;
    private ArrayList<ProjectResource> resourceArrayList;


    public Task() {
    }

    public Task(String name, String startDate, String endDate, double cost) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cost = cost;
    }

    public Task(String name, String startDate, String endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cost = 0;
    }


    public Task(String name, String startDate, String endDate,ArrayList<ProjectResource> resourceArrayList, double cost) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cost = cost;
        this.resourceArrayList = resourceArrayList;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public ArrayList<ProjectResource> getResourceArrayList() {
        return resourceArrayList;
    }

    public void setResourceArrayList(ArrayList<ProjectResource> resourceArrayList) {
        this.resourceArrayList = resourceArrayList;
    }
}
