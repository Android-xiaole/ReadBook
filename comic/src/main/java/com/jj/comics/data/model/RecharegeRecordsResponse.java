package com.jj.comics.data.model;


import java.util.List;

public class RecharegeRecordsResponse extends ResponseModel {

    private List<RechargeRecordModel> data;

    public List<RechargeRecordModel> getData() {
        return data;
    }

    public void setData(List<RechargeRecordModel> data) {
        this.data = data;
    }

}
