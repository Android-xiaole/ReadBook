package com.jj.comics.data.model;

public class CashOutResponse extends ResponseModel {

    /**
     * data : {"status":true}
     */

    private CashOutResponse.DataBean data;

    public CashOutResponse.DataBean getData() {
        return data;
    }

    public void setData(CashOutResponse.DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * status : true
         */

        private boolean status;

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }
    }
}
