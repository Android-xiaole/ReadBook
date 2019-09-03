package com.jj.comics.data.model;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.jj.comics.adapter.mine.WelfareItemAdapter;

public class TaskGroup extends AbstractExpandableItem<TaskModel> implements MultiItemEntity {
    private String productCode;
    private String taskCode;
    private String taskName;
    private boolean enabled;
    private String info;
    private Tasks tasks;
    private TaskType taskType;

    public TaskGroup() {
    }

    public TaskGroup(String productCode, String taskCode, String taskName, boolean enabled, String info, Tasks tasks, TaskType taskType) {
        this.productCode = productCode;
        this.taskCode = taskCode;
        this.taskName = taskName;
        this.enabled = enabled;
        this.info = info;
        this.tasks = tasks;
        this.taskType = taskType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Tasks getTasks() {
        return tasks;
    }

    public void setTasks(Tasks tasks) {
        this.tasks = tasks;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    @Override
    public int getItemType() {
        return WelfareItemAdapter.TYPE_LEVEL_0;
    }

    @Override
    public int getLevel() {
        return 0;
    }
}
