package com.jj.comics.data.model;

public class SignAutoResponse extends ResponseModel{

    /**
     * code : 200
     * message : 请求成功
     * data : {"is_check":0,"is_autoby":0}
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
         * is_check : 0
         * is_autoby : 0
         */

        private int is_check;
        private int is_autoby;

        public int getIs_check() {
            return is_check;
        }

        public void setIs_check(int is_check) {
            this.is_check = is_check;
        }

        public int getIs_autoby() {
            return is_autoby;
        }

        public void setIs_autoby(int is_autoby) {
            this.is_autoby = is_autoby;
        }
    }
}
