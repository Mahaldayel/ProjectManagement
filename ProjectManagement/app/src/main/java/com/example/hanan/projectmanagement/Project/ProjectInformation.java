package com.example.hanan.projectmanagement.Project;

public class ProjectInformation {

    private String name;
    private String startDate;
    private String endDate;
    private Double totalCost=0.0;


    public ProjectInformation() {
    }

    public ProjectInformation(String name, String startDate, String endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalCost = 0.0;
    }
    public ProjectInformation(String name, String startDate, String endDate,Double totalCost) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalCost = totalCost;
    }
    public String getName() {
        return name;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }
}
