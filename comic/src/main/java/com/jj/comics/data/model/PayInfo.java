package com.jj.comics.data.model;

import java.io.Serializable;

/**
 * 用户支付相关信息
 */
public class PayInfo extends ResponseModel implements Serializable {


    /**
     * id : 0
     * pay_openid :用户的支付 openid
     * unionid : 账户统一唯一标识
     * pay : 支付次数
     * voucher : 书券
     * egold : 金币数
     * zb : 赠币数
     * is_vip : 是否是vip 1:是，0：否
     * vip_enddate : VIP到期时间
     * is_svip : 是否是svip 1:是，0：否
     * svip_enddate : SVIP到期时间
     * create_time : 2019-07-17 10:46:10
     * update_time : 2019-07-17 10:46:10
     */

    private long id;
    private String pay_openid;
    private String unionid;
    private int pay;
    private int voucher;
    private int egold;
    private int zb;
    private int total_egold;
    private int is_vip;
    private int is_svip;
    private long vip_enddate;
    private long svip_enddate;
    private int vip_endday;
    private int svip_endday;
    private String create_time;
    private String update_time;
    private float total_rebate_amount;
    private float total_drawcash_amount;
    private float can_drawout_amount;
    private float newest_rebate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPay_openid() {
        return pay_openid;
    }

    public void setPay_openid(String pay_openid) {
        this.pay_openid = pay_openid;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public int getPay() {
        return pay;
    }

    public void setPay(int pay) {
        this.pay = pay;
    }

    public int getVoucher() {
        return voucher;
    }

    public void setVoucher(int voucher) {
        this.voucher = voucher;
    }

    public int getEgold() {
        return egold;
    }

    public void setEgold(int egold) {
        this.egold = egold;
    }

    public int getZb() {
        return zb;
    }

    public void setZb(int zb) {
        this.zb = zb;
    }

    public int getTotal_egold() {
        return total_egold;
    }

    public void setTotal_egold(int total_egold) {
        this.total_egold = total_egold;
    }

    public int getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(int is_vip) {
        this.is_vip = is_vip;
    }

    public int getIs_svip() {
        return is_svip;
    }

    public void setIs_svip(int is_svip) {
        this.is_svip = is_svip;
    }

    public long getVip_enddate() {
        return vip_enddate;
    }

    public void setVip_enddate(long vip_enddate) {
        this.vip_enddate = vip_enddate;
    }

    public long getSvip_enddate() {
        return svip_enddate;
    }

    public void setSvip_enddate(long svip_enddate) {
        this.svip_enddate = svip_enddate;
    }

    public int getVip_endday() {
        return vip_endday;
    }

    public void setVip_endday(int vip_endday) {
        this.vip_endday = vip_endday;
    }

    public int getSvip_endday() {
        return svip_endday;
    }

    public void setSvip_endday(int svip_endday) {
        this.svip_endday = svip_endday;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public float getTotal_rebate_amount() {
        return total_rebate_amount;
    }

    public void setTotal_rebate_amount(float total_rebate_amount) {
        this.total_rebate_amount = total_rebate_amount;
    }

    public float getTotal_drawcash_amount() {
        return total_drawcash_amount;
    }

    public void setTotal_drawcash_amount(float total_drawcash_amount) {
        this.total_drawcash_amount = total_drawcash_amount;
    }

    public float getCan_drawout_amount() {
        return can_drawout_amount;
    }

    public void setCan_drawout_amount(float can_drawout_amount) {
        this.can_drawout_amount = can_drawout_amount;
    }

    public float getNewest_rebate() {
        return newest_rebate;
    }

    public void setNewest_rebate(float newest_rebate) {
        this.newest_rebate = newest_rebate;
    }
}
