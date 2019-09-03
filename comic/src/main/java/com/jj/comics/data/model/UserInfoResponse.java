package com.jj.comics.data.model;

/**
 * 获取用户信息返回值
 */
public class UserInfoResponse extends ResponseModel {


    /**
     * data : {"baseinfo":{"uid":28547,"appid":null,"openid":"15056007765","pay_openid":null,"unionid":"15056007765","sourceid":1,"username":"app19071118322741263","nickname":"15056007765","avatar":"","mobile":15056007765,"sex":0,"pos_city":"--","password":"","agentid":0,"spreadid":0,"wxkeywordid":0,"is_attention":0,"auto_buy":0,"login":12,"reg_ip":null,"reg_time":null,"last_login_ip":"172.19.22.87","last_login_time":1563255980,"from_channel_id":0,"from_agent_id":0,"status":1,"create_time":"2019-07-11 18:32:27","update_time":"2019-07-16 13:46:20","delete_flag":0},"payinfo":{"id":0,"pay_openid":"","unionid":"15056007765","pay":0,"voucher":0,"egold":0,"zb":0,"is_vip":0,"vip_enddate":0,"is_svip":0,"svip_enddate":0,"create_time":"2019-07-16 13:46:20","update_time":"2019-07-16 13:46:20"}}
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
         * baseinfo : {"uid":28547,"appid":null,"openid":"15056007765","pay_openid":null,"unionid":"15056007765","sourceid":1,"username":"app19071118322741263","nickname":"15056007765","avatar":"","mobile":15056007765,"sex":0,"pos_city":"--","password":"","agentid":0,"spreadid":0,"wxkeywordid":0,"is_attention":0,"auto_buy":0,"login":12,"reg_ip":null,"reg_time":null,"last_login_ip":"172.19.22.87","last_login_time":1563255980,"from_channel_id":0,"from_agent_id":0,"status":1,"create_time":"2019-07-11 18:32:27","update_time":"2019-07-16 13:46:20","delete_flag":0}
         */

        private UserInfo baseinfo;

        public UserInfo getBaseinfo() {
            return baseinfo;
        }

        public void setBaseinfo(UserInfo baseinfo) {
            this.baseinfo = baseinfo;
        }

    }
}
