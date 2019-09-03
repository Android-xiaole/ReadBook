package com.jj.comics.data.model;

import java.util.List;

public class FeedbackListResponse extends ResponseModel{


    private List<FeedbackModel> data;

    public List<FeedbackModel> getData() {
        return data;
    }

    public void setData(List<FeedbackModel> data) {
        this.data = data;
    }

}
