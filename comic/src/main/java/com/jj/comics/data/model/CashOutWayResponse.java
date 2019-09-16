package com.jj.comics.data.model;

public class CashOutWayResponse extends ResponseModel {

    /**
     * data : {"alipay":true,"bank":true}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * alipay : true
         * bank : true
         */

        private boolean alipay;
        private boolean bank;

        public boolean isAlipay() {
            return alipay;
        }

        public void setAlipay(boolean alipay) {
            this.alipay = alipay;
        }

        public boolean isBank() {
            return bank;
        }

        public void setBank(boolean bank) {
            this.bank = bank;
        }
    }
}
