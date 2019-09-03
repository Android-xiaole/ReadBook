package com.jj.comics.data.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.jj.base.mvp.IModel;
import com.jj.base.net.NetError;
import com.jj.comics.adapter.mine.WelfareItemAdapter;

import java.util.List;

public class Tasks implements IModel, MultiItemEntity {
    private List<TaskModel> taskList;

    public Tasks() {
    }

    public Tasks(List<TaskModel> taskList) {
        this.taskList = taskList;
    }

    public List<TaskModel> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<TaskModel> taskList) {
        this.taskList = taskList;
    }

    @Override
    public NetError error() {
        return null;
    }

    @Override
    public int getItemType() {
        return WelfareItemAdapter.TYPE_LEVEL_1;
    }
}
