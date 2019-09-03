package com.jj.comics.data.model;


import androidx.annotation.NonNull;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.jj.comics.adapter.mine.WelfareItemAdapter;

public class TaskModel implements Comparable<TaskModel>,MultiItemEntity {
    private String productCode;
    private String code;
    private String name;
    private int taskReward;
    private String info;
    private boolean complete;
    private boolean getReward;
    private String taskIcon;

    public TaskModel() {
    }

    public TaskModel(String productCode, String code, String name, int taskReward, String info, boolean complete, boolean getReward, String taskIcon) {
        this.productCode = productCode;
        this.code = code;
        this.name = name;
        this.taskReward = taskReward;
        this.info = info;
        this.complete = complete;
        this.getReward = getReward;
        this.taskIcon = taskIcon;
    }

    public String getTaskIcon() {
        return taskIcon;
    }

    public void setTaskIcon(String taskIcon) {
        this.taskIcon = taskIcon;
    }

    public boolean isGetReward() {
        return getReward;
    }

    public void setGetReward(boolean getReward) {
        this.getReward = getReward;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTaskReward() {
        return taskReward;
    }

    public void setTaskReward(int taskReward) {
        this.taskReward = taskReward;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    @Override
    public int getItemType() {
        return WelfareItemAdapter.TYPE_LEVEL_1;
    }

    @Override
    public int compareTo(@NonNull TaskModel o) {
        return 0;
    }
}
