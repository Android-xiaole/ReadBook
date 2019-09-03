package com.jj.comics.data.model;

public class PayInfoResponse extends ResponseModel{


    /**
     * data : {"id":0,"pay_openid":"","unionid":"15056007765","pay":0,"voucher":0,"egold":0,"zb":0,"is_vip":0,"vip_enddate":0,"is_svip":0,"svip_enddate":0,"create_time":"2019-07-17 10:46:10","update_time":"2019-07-17 10:46:10"}
     */

    private PayInfo data;

    public PayInfo getData() {
        return data;
    }

    public void setData(PayInfo data) {
        this.data = data;
    }
}
