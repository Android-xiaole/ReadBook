package com.jj.comics.data.model;

public class PrePayOrderResponseWx extends ResponseModel {


    /**
     * data : {"return_code":"SUCCESS","return_msg":"OK","appid":"wx852297c06ef6ba77","mch_id":"1526221031","nonce_str":"CfFuYFtmv8OiSRtM","sign":"D2C5E1214687B8C8A2CFB8AF5FB17FE7","result_code":"SUCCESS","prepay_id":"wx271422483356883440bc6ac11592132000","trade_type":"APP"}
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
         * return_code : SUCCESS
         * return_msg : OK
         * appid : wx852297c06ef6ba77
         * mch_id : 1526221031
         * nonce_str : CfFuYFtmv8OiSRtM
         * sign : D2C5E1214687B8C8A2CFB8AF5FB17FE7
         * result_code : SUCCESS
         * prepay_id : wx271422483356883440bc6ac11592132000
         * trade_type : APP
         */

        private String return_code;
        private String return_msg;
        private String appid;
        private String mch_id;
        private String nonce_str;
        private String sign;
        private String result_code;
        private String prepay_id;
        private String trade_type;
        private long timestamp;

        public long getTimeStamp() {
            return timestamp;
        }

        public void setTimeStamp(long timeStamp) {
            this.timestamp = timeStamp;
        }

        public String getReturn_code() {
            return return_code;
        }

        public void setReturn_code(String return_code) {
            this.return_code = return_code;
        }

        public String getReturn_msg() {
            return return_msg;
        }

        public void setReturn_msg(String return_msg) {
            this.return_msg = return_msg;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getMch_id() {
            return mch_id;
        }

        public void setMch_id(String mch_id) {
            this.mch_id = mch_id;
        }

        public String getNonce_str() {
            return nonce_str;
        }

        public void setNonce_str(String nonce_str) {
            this.nonce_str = nonce_str;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getResult_code() {
            return result_code;
        }

        public void setResult_code(String result_code) {
            this.result_code = result_code;
        }

        public String getPrepay_id() {
            return prepay_id;
        }

        public void setPrepay_id(String prepay_id) {
            this.prepay_id = prepay_id;
        }

        public String getTrade_type() {
            return trade_type;
        }

        public void setTrade_type(String trade_type) {
            this.trade_type = trade_type;
        }
    }
}
