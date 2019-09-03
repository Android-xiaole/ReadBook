package com.jj.comics.data.model;

public class PrePayOrderResponseHuifubao extends ResponseModel {

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String tokenId;
        private String agentId;
        private String agentBillId;
        private String pay_type;

        public String getAgentId() {
            return agentId;
        }

        public void setAgentId(String agentId) {
            this.agentId = agentId;
        }

        public String getAgentBillId() {
            return agentBillId;
        }

        public void setAgentBillId(String agentBillId) {
            this.agentBillId = agentBillId;
        }

        public String getPay_type() {
            return pay_type;
        }

        public void setPay_type(String pay_type) {
            this.pay_type = pay_type;
        }

        public String getTokenId() {
            return tokenId;
        }

        public void setTokenId(String tokenId) {
            this.tokenId = tokenId;
        }
    }
}
