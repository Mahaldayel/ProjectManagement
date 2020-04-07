package com.example.hanan.projectmanagement.Recourse;

public class ProjectResource {

    private String name;
    private String type;
    private double hourCost;
    private int numberOfhours;

    public ProjectResource(String name, String type, double hourCost, int numberOfhours) {
        this.name = name;
        this.type = type;
        this.hourCost = hourCost;
        this.numberOfhours = numberOfhours;
    }

    public ProjectResource() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getHourCost() {
        return hourCost;
    }

    public void setHourCost(double hourCost) {
        this.hourCost = hourCost;
    }

    public int getNumberOfhours() {
        return numberOfhours;
    }

    public void setNumberOfhours(int numberOfhours) {
        this.numberOfhours = numberOfhours;
    }
}
