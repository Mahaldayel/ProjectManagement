package com.example.hanan.projectmanagement.Task;

import com.example.hanan.projectmanagement.Resourse;

import java.util.ArrayList;

public class Task {


    private String name;
    private String startDate;
    private String endDate;
    private double cost;
    private ArrayList<Resourse> resourseArrayList;


    public Task() {
    }

    public Task(String name, String startDate, String endDate, double cost) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cost = cost;
        this.resourseArrayList = resourseArrayList;
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

    public ArrayList<Resourse> getResourseArrayList() {
        return resourseArrayList;
    }

    public void setResourseArrayList(ArrayList<Resourse> resourseArrayList) {
        this.resourseArrayList = resourseArrayList;
    }
}
