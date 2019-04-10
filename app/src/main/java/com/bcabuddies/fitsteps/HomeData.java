package com.bcabuddies.fitsteps;

import java.util.Date;

public class HomeData extends HomeDataId {

    private String distance, name, uid, calories, steps;
    private Date time;

    public HomeData() {
    }

    public HomeData(String distance, String name, String uid, String calories, String steps, Date time) {
        this.distance = distance;
        this.name = name;
        this.uid = uid;
        this.calories = calories;
        this.steps = steps;
        this.time = time;
    }

    String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
