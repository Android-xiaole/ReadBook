package com.jj.comics.data.model;

import android.text.TextUtils;

import com.jj.base.mvp.IModel;
import com.jj.base.net.NetError;

import java.util.List;

public class ProtocalModel implements IModel {

    List<Protocal> productProtocolList;

    Protocal productProtocol;

    @Override
    public NetError error() {
        if (productProtocolList == null && productProtocol == null)
            return NetError.noDataError();
        return null;
    }

    public Protocal getProductProtocol() {
        return productProtocol;
    }

    public void setProductProtocol(Protocal productProtocol) {
        this.productProtocol = productProtocol;
    }

    public List<Protocal> getProductProtocolList() {
        return productProtocolList;
    }

    public void setProductProtocolList(List<Protocal> productProtocolList) {
        this.productProtocolList = productProtocolList;
    }

    public class Protocal implements IModel {
        String productCode;
        String protocol;
        String protocolName;
        String describe;

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public String getProtocolName() {
            return protocolName;
        }

        public void setProtocolName(String protocolName) {
            this.protocolName = protocolName;
        }

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }


        @Override
        public NetError error() {
            if (TextUtils.isEmpty(describe) || TextUtils.isEmpty(protocolName)
                    || TextUtils.isEmpty(protocol))
                return NetError.noDataError();
            return null;
        }
    }
}
