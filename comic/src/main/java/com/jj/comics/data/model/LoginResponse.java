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

        private boolean is_first;//表示该用户是否第一次登录

        public boolean isIs_first() {
            return is_first;
        }

        public void setIs_first(boolean is_first) {
            this.is_first = is_first;
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