package com.jj.comics.data.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class GoodsPriceModel implements MultiItemEntity {

    /**
     * id : 10
     * productCode : manhua
     * packagesCode : vip
     * code : super
     * name : 超级会员
     * title1 :
     * title2 :
     * title3 :
     * price1 : 498.00
     * price2 : 365.00
     * enabled : 1
     * info : 一年之内 全站漫画随便看
     * present : true
     * persentCoupons : 10000
     * exchangeScale : 100
     * changeFont : true
     */
    public static final int VIP = 1;
    public static final int SHUBI = 2;
    private long id;
    private String productCode;
    private String packagesCode;
    private String code;
    private String name;
    private String title1;
    private String title2;
    private String title3;
    private String price1;
    private String price2;
    private int enabled;
    private String info;
    private boolean present;
    private int persentCoupons;
    private int secondPersentCoupons;
    private int exchangeScale;
    private boolean changeFont;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getPackagesCode() {
        return packagesCode;
    }

    public void setPackagesCode(String packagesCode) {
        this.packagesCode = packagesCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public String getTitle3() {
        return title3;
    }

    public void setTitle3(String title3) {
        this.title3 = title3;
    }

    public String getPrice1() {
        return price1;
    }

    public void setPrice1(String price1) {
        this.price1 = price1;
    }

    public String getPrice2() {
        if (TextUtils.isEmpty(price2) || TextUtils.equals("null", price1)) return "0.00";
        return price2;
    }

    public void setSecondPersentCoupons(int secondPersentCoupons) {
        this.secondPersentCoupons = secondPersentCoupons;
    }

    public int getSecondPersentCoupons() {
        return secondPersentCoupons;
    }

    public void setPrice2(String price2) {
        this.price2 = price2;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public int getPersentCoupons() {
        return persentCoupons;
    }

    public void setPersentCoupons(int persentCoupons) {
        this.persentCoupons = persentCoupons;
    }

    public int getExchangeScale() {
        return exchangeScale;
    }

    public void setExchangeScale(int exchangeScale) {
        this.exchangeScale = exchangeScale;
    }

    public boolean isChangeFont() {
        return changeFont;
    }

    public void setChangeFont(boolean changeFont) {
        this.changeFont = changeFont;
    }

    @Override
    public int getItemType() {
        if (TextUtils.equals(packagesCode, "vip"))
            return VIP;
        return SHUBI;
    }
}
