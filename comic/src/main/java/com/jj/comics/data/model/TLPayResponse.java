package com.jj.comics.data.model;


public class TLPayResponse extends ResponseModel {

    /**
     * data : {"pay_content":"https://qr.alipay.com/bax055456f4rd6dljpvy6023"}
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
         * pay_content : https://qr.alipay.com/bax055456f4rd6dljpvy6023
         */

        private String pay_content;
        private String out_trade_no;

        public String getOut_trade_no() {
            return out_trade_no;
        }

        public void setOut_trade_no(String out_trade_no) {
            this.out_trade_no = out_trade_no;
        }

        public String getPay_content() {
            return pay_content;
        }

        public void setPay_content(String pay_content) {
            this.pay_content = pay_content;
        }
    }
}
