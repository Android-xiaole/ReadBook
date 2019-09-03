package com.jj.comics.data.model;

import java.util.List;

/**
 * 返回书的列表的通用模型，只有一层data
 */
public class BookListResponse extends ResponseModel{

    private List<BookModel> data;

    public List<BookModel> getData() {
        return data;
    }

    public void setData(List<BookModel> data) {
        this.data = data;
    }
}
