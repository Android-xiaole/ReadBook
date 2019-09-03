//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jj.comics.data.model;

public enum ProductPayTypeEnum {
    AliPay("alipay", "支付宝"),
    WeChat("wcpay", "微信"),
    HuFei("huafei", "话费"),
    Speedy("speedy", "快捷支付"),
    UnionPay("unionpay", "银联");
    private String code;
    private String name;

    private ProductPayTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public static ProductPayTypeEnum getTypeByCode(String code) {
        if (code == null) {
            return null;
        } else {
            ProductPayTypeEnum[] var4;
            int var3 = (var4 = values()).length;

            for (int var2 = 0; var2 < var3; ++var2) {
                ProductPayTypeEnum type = var4[var2];
                if (type.getCode().equals(code)) {
                    return type;
                }
            }

            return null;
        }
    }
}
