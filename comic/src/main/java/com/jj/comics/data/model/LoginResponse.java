package com.jj.comics.data.model;

public class LoginResponse extends ResponseModel {


    /**
     * data : {"bearer_token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vY2FydG9vbi1ub3ZlbC50eHJlYWQubmV0L2FwaS9tbG9naW4iLCJpYXQiOjE1NjI4NDExNDcsImV4cCI6MTg3Mzg4MTE0NywibmJmIjoxNTYyODQxMTQ3LCJqdGkiOiJQUUd3V1pVYXluNGNQd1RhIiwic3ViIjoyODU0NywicHJ2IjoiMjNiZDVjODk0OWY2MDBhZGIzOWU3MDFjNDAwODcyZGI3YTU5NzZmNyIsInVpZCI6bnVsbCwib3BlbmlkIjoiMTUwNTYwMDc3NjUiLCJ1bmlvbmlkIjoiMTUwNTYwMDc3NjUiLCJuaWNrbmFtZSI6IjE1MDU2MDA3NzY1IiwiYXZhdGFyIjoiIn0.jLaQ5Dk6adN8JkZEMD4xGuxY6QqlxsSdcWlC5Xp3n_4","user_info":{"openid":"15056007765","unionid":"15056007765","sourceid":1,"username":"app19071118322741263","password":"","nickname":"15056007765","salt":"","avatar":"","mobile":"15056007765","sex":0,"pos_city":"--","status":1,"update_time":"2019-07-11 18:32:27","create_time":"2019-07-11 18:32:27","uid":28547}}
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
         * bearer_token : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vY2FydG9vbi1ub3ZlbC50eHJlYWQubmV0L2FwaS9tbG9naW4iLCJpYXQiOjE1NjI4NDExNDcsImV4cCI6MTg3Mzg4MTE0NywibmJmIjoxNTYyODQxMTQ3LCJqdGkiOiJQUUd3V1pVYXluNGNQd1RhIiwic3ViIjoyODU0NywicHJ2IjoiMjNiZDVjODk0OWY2MDBhZGIzOWU3MDFjNDAwODcyZGI3YTU5NzZmNyIsInVpZCI6bnVsbCwib3BlbmlkIjoiMTUwNTYwMDc3NjUiLCJ1bmlvbmlkIjoiMTUwNTYwMDc3NjUiLCJuaWNrbmFtZSI6IjE1MDU2MDA3NzY1IiwiYXZhdGFyIjoiIn0.jLaQ5Dk6adN8JkZEMD4xGuxY6QqlxsSdcWlC5Xp3n_4
         * user_info : {"openid":"15056007765","unionid":"15056007765","sourceid":1,"username":"app19071118322741263","password":"","nickname":"15056007765","salt":"","avatar":"","mobile":"15056007765","sex":0,"pos_city":"--","status":1,"update_time":"2019-07-11 18:32:27","create_time":"2019-07-11 18:32:27","uid":28547}
         */

        private String bearer_token;
        private UserInfo user_info;

        //下面三个字段如果第三方登录没有绑定手机号才会返回，code=400
        private String openid;//自己后台的openid
        private String type;//第三方登录类型
        private boolean is_binding;//是否绑定

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isIs_binding() {
            return is_binding;
        }

        public void setIs_binding(boolean is_binding) {
            this.is_binding = is_binding;
        }

        public void setBearer_token(String bearer_token) {
            this.bearer_token = bearer_token;
        }

        public String getBearer_token() {
            return bearer_token;
        }

        public UserInfo getUser_info() {
            return user_info;
        }

        public void setUser_info(UserInfo user_info) {
            this.user_info = user_info;
        }
    }
}