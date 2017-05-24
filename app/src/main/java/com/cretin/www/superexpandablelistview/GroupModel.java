package com.cretin.www.superexpandablelistview;

/**
 * Created by cretin on 2017/5/23.
 */

public class GroupModel {
    private String title;
    private String online;

    public GroupModel(String title, String online) {
        this.title = title;
        this.online = online;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }
}
