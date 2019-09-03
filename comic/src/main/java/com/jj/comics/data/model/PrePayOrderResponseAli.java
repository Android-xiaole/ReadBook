package com.jj.comics.data.model;

public class PrePayOrderResponseAli extends ResponseModel {

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String pay_content;

        public String getPay_content() {
            return pay_content;
        }

        public void setPay_content(String pay_content) {
            this.pay_content = pay_content;
        }
    }
}
