package com.example.gochats.modes;

import java.util.ArrayList;

public class UserStatus {

    private String Name;
    private String Image;
    private long lastUpdated;
    private ArrayList<Status>statuses;

    public UserStatus() {
    }

    public UserStatus(String name, String image, long lastUpdated, ArrayList<Status> statuses) {
        Name = name;
        Image = image;
        this.lastUpdated = lastUpdated;
        this.statuses = statuses;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public ArrayList<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<Status> statuses) {
        this.statuses = statuses;
    }
}
