package com.jj.comics.data.model;

import java.util.List;

public class RichResponse extends ResponseModel {

    private List<RichManModel> data;

    public List<RichManModel> getData() {
        return data;
    }

    public void setData(List<RichManModel> data) {
        this.data = data;
    }


}
