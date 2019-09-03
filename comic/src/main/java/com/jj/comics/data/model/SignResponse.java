package com.jj.comics.data.model;

public class SignResponse extends ResponseModel {


    /**
     * code : 200
     * message : 您今天已经签到过了
     * data : {"code":200}
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
         * code : 200
         */

        private int code;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }
}
