package com.jj.comics.data.model;

import com.jj.base.net.NetError;

import java.util.List;

public class BookCatalogListResponse extends ResponseModel {

    @Override
    public NetError error() {
        if (code == 200) {
            if (data == null || data.getData() == null) {
                return NetError.noDataError();
            } else {
                return null;
            }
        } else {
            return new NetError(message, code);
        }
    }

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {

        private int total_num;
        private List<BookCatalogModel> data;

        public int getTotal_num() {
            return total_num;
        }

        public void setTotal_num(int total_num) {
            this.total_num = total_num;
        }

        public List<BookCatalogModel> getData() {
            return data;
        }

        public void setData(List<BookCatalogModel> data) {
            this.data = data;
        }
    }


}
