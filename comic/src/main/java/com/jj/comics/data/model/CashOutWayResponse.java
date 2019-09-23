package com.jj.comics.data.model;


public class CashOutWayResponse extends ResponseModel {


    /**
     * data : {"alipay":{"status":false,"information":{"account_number":"","opener":""}},"bank":{"status":false,"information":{"account_number":"","opener":"","opening_bank":""}}}
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
         * alipay : {"status":false,"information":{"account_number":"","opener":""}}
         * bank : {"status":false,"information":{"account_number":"","opener":"","opening_bank":""}}
         */

        private AlipayBean alipay;
        private BankBean bank;

        public AlipayBean getAlipay() {
            return alipay;
        }

        public void setAlipay(AlipayBean alipay) {
            this.alipay = alipay;
        }

        public BankBean getBank() {
            return bank;
        }

        public void setBank(BankBean bank) {
            this.bank = bank;
        }

    }
}
