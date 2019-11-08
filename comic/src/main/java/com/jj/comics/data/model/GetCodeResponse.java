package com.jj.comics.data.model;

/**
 * Author ：le
 * Date ：2019-11-07 16:31
 * Description ：普通的获取验证码返回
 */
public class GetCodeResponse extends ResponseModel{

    /**
     * data : {"is_first":true}
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
         * is_first : true
         */

        private boolean is_first;

        public boolean isIs_first() {
            return is_first;
        }

        public void setIs_first(boolean is_first) {
            this.is_first = is_first;
        }
    }
}
