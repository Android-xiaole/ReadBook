package com.jj.comics.data.model;

import com.jj.base.net.NetError;

/**
 * data 只返回一个boolean类型的status字段的通用模型
 */
public class CommonStatusResponse extends ResponseModel {

    /**
     * data : {"status":true}
     */

    private DataBean data;

    @Override
    public NetError error() {
        if (data != null) {//data有返回值,再回调到onNext里面
            return null;
        }
        return new NetError(message, code);
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * status : true
         */

        private boolean status;

        public boolean getStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }
    }
}
