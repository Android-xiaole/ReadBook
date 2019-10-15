package com.jj.comics.data.model;

public class RestResponse extends ResponseModel {

    /**
     * data : {"is_autoby":0,"is_receive":1}
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
         * is_autoby : 0
         * is_receive : 1
         */

        private int is_autoby;
        private int is_receive;

        public int getIs_autoby() {
            return is_autoby;
        }

        public void setIs_autoby(int is_autoby) {
            this.is_autoby = is_autoby;
        }

        public int getIs_receive() {
            return is_receive;
        }

        public void setIs_receive(int is_receive) {
            this.is_receive = is_receive;
        }
    }
}
