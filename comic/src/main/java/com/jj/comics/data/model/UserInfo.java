package com.jj.comics.data.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

@Entity
public class UserInfo implements Serializable {

    private static final long serialVersionUID = -5308893968273725711L;
    /**
     * userId : 3439
     * gender : 0
     * age : 0
     * name :
     * nickName : 用户21735576901
     * imageUrl :
     * mobile : 13944462089
     * registerTime : 2018-12-06 17:44:44.0
     * coin : 0
     * identity : 9b69c52f4a962ece
     * isVip : false
     * vipLevel : 0
     * vipName :
     * currencyBalance : 0
     * signin : false
     * signinCnt : 0
     * tempUser : false
     * userAccounts : [{"accountNo":"5862515659520181206174444","balance":0,"openAccountTime":"2018-12-06 17:44:44.0"}]
     * coupons : 0
     * subscribeMp : false
     * openId :
     * newUser : false
     * platform : null
     * imsi :
     * imei :
     * openidWay :
     * appid :
     * spreadId : null
     */

    @Id
    private long uid;
    private String openid;
    private String unionid;
    private int sourceid;
    private String username;
    private String password;
    private String nickname;
    private String salt;
    private String avatar;
    private long mobile;
    private int sex;
    private String pos_city;
    private int status;
    private String create_time;
    private String update_time;
    private String invite_code;
    private int delete_flag;
    private int is_vip;//表示用户是否是vip，1是：0否
    private int is_free;//表示用户是否可以免费阅读，1是：0否

    public String getLogin_type() {
        return login_type;
    }

    public void setLogin_type(String login_type) {
        this.login_type = login_type;
    }

    private String login_type;

    //自定义字段，标记当前用户是否登录
    private boolean isLogin = false;
    private String class_name;//用户级别描述

    public long getUid() {
        return this.uid;
    }
    public void setUid(long uid) {
        this.uid = uid;
    }
    public String getOpenid() {
        return this.openid;
    }
    public void setOpenid(String openid) {
        this.openid = openid;
    }
    public String getUnionid() {
        return this.unionid;
    }
    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
    public int getSourceid() {
        return this.sourceid;
    }
    public void setSourceid(int sourceid) {
        this.sourceid = sourceid;
    }
    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getNickname() {
        return this.nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getSalt() {
        return this.salt;
    }
    public void setSalt(String salt) {
        this.salt = salt;
    }
    public String getAvatar() {
        return this.avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public long getMobile() {
        return this.mobile;
    }
    public void setMobile(long mobile) {
        this.mobile = mobile;
    }
    public int getSex() {
        return this.sex;
    }
    public void setSex(int sex) {
        this.sex = sex;
    }
    public String getPos_city() {
        return this.pos_city;
    }
    public void setPos_city(String pos_city) {
        this.pos_city = pos_city;
    }
    public int getStatus() {
        return this.status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getCreate_time() {
        return this.create_time;
    }
    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
    public String getUpdate_time() {
        return this.update_time;
    }
    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }
    public String getInvite_code() {
        return this.invite_code;
    }
    public void setInvite_code(String invite_code) {
        this.invite_code = invite_code;
    }
    public int getDelete_flag() {
        return this.delete_flag;
    }
    public void setDelete_flag(int delete_flag) {
        this.delete_flag = delete_flag;
    }
    public int getIs_vip() {
        return this.is_vip;
    }
    public void setIs_vip(int is_vip) {
        this.is_vip = is_vip;
    }
    public boolean getIsLogin() {
        return this.isLogin;
    }
    public void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }
    public String getClass_name() {
        return this.class_name;
    }
    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }
    public int getIs_free() {
        return this.is_free;
    }
    public void setIs_free(int is_free) {
        this.is_free = is_free;
    }


}
