package com.jj.comics.data.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.jj.base.mvp.IModel;
import com.jj.base.net.NetError;

import java.util.List;

public class TaskGroups implements IModel, MultiItemEntity {
    private List<TaskGroup> taskGroupList;

    public TaskGroups() {
    }

    public TaskGroups(List<TaskGroup> taskGroupList) {
        this.taskGroupList = taskGroupList;
    }

    public List<TaskGroup> getTaskGroupList() {
        return taskGroupList;
    }

    public void setTaskGroupList(List<TaskGroup> taskGroupList) {
        this.taskGroupList = taskGroupList;
    }

    @Override
    public NetError error() {
        return null;
    }

    @Override
    public int getItemType() {
        return 1;
    }
}
