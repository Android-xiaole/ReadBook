package com.jj.comics.data.model;

import java.util.List;

public class BookListPopShareResponse extends ResponseModel {

    private List<BookModel> data;

    public List<BookModel> getData() {
        return data;
    }

    public void setData(List<BookModel> data) {
        this.data = data;
    }

}
