package com.hensonyung.phonesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by wengjiasheng on 2015/4/1.
 */
public class TaskInfo {
    private Drawable icon;
    private String name;
    private String packname;
    private long memSize;
    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    private boolean userTask;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackname() {
        return packname;
    }

    public void setPackname(String packname) {
        this.packname = packname;
    }

    public long getMemSize() {
        return memSize;
    }

    public void setMemSize(long memSize) {
        this.memSize = memSize;
    }

    public boolean isUserTask() {
        return userTask;
    }

    public void setUserTask(boolean userTask) {
        this.userTask = userTask;
    }

    @Override
    public String toString() {
        return "TaskInfo{" +
                "icon=" + icon +
                ", name='" + name + '\'' +
                ", packname='" + packname + '\'' +
                ", memSize=" + memSize +
                ", userTask=" + userTask +
                '}';
    }
}
