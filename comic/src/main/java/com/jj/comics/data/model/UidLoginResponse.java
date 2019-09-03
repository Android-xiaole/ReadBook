package com.jj.comics.data.model;

public class UidLoginResponse extends ResponseModel {

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {

        /**
         * bearer_token : 每个接口的鉴权token
         * user_info : （数组） 用户的基本信息
         */

        private String bearer_token;
        private UserInfo user_info;

        public String getBearer_token() {
            return bearer_token;
        }

        public void setBearer_token(String bearer_token) {
            this.bearer_token = bearer_token;
        }

        public UserInfo getUser_info() {
            return user_info;
        }

        public void setUser_info(UserInfo user_info) {
            this.user_info = user_info;
        }
    }
}
