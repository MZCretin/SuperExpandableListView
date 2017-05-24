package com.cretin.www.superexpandablelistview;

/**
 * Created by cretin on 2017/5/23.
 */

public class ChildModel {
    private String name;
    private String sig;

    public ChildModel(String name, String sig) {
        this.name = name;
        this.sig = sig;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }
}
