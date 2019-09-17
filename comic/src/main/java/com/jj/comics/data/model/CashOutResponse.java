package com.jj.comics.data.model;

import java.io.Serializable;

public class CashOutResponse extends ResponseModel{


    /**
     * data : {"money":3.3,"serial_number":4868826157205996,"time":"2019-09-17 14:01:42","payee_account":"ddd****ddd","payee_name":"李*","status":true}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Serializable{
        /**
         * money : 3.3
         * serial_number : 4868826157205996
         * time : 2019-09-17 14:01:42
         * payee_account : ddd****ddd
         * payee_name : 李*
         * status : true
         */

        private double money;
        private long serial_number;
        private String time;
        private String payee_account;
        private String payee_name;
        private boolean status;
        private int type;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public double getMoney() {
            return money;
        }

        public void setMoney(double money) {
            this.money = money;
        }

        public long getSerial_number() {
            return serial_number;
        }

        public void setSerial_number(long serial_number) {
            this.serial_number = serial_number;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getPayee_account() {
            return payee_account;
        }

        public void setPayee_account(String payee_account) {
            this.payee_account = payee_account;
        }

        public String getPayee_name() {
            return payee_name;
        }

        public void setPayee_name(String payee_name) {
            this.payee_name = payee_name;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }
    }
}
