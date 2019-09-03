package com.jj.comics.data.model;

public class TaskType {
    private int id;
    private String productCode;
    private String taskTypeCode;
    private String name;

    public TaskType() {
    }

    public TaskType(int id, String productCode, String taskTypeCode, String name) {
        this.id = id;
        this.productCode = productCode;
        this.taskTypeCode = taskTypeCode;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getTaskTypeCode() {
        return taskTypeCode;
    }

    public void setTaskTypeCode(String taskTypeCode) {
        this.taskTypeCode = taskTypeCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
