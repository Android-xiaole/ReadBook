package com.jj.comics.data.model;

import android.text.TextUtils;

import com.jj.base.mvp.IModel;
import com.jj.base.net.NetError;

public class WxModel implements IModel {
    private String wxId;

    public void setWxId(String wxId) {
        this.wxId = wxId;
    }

    public String getWxId() {
        return wxId;
    }

    @Override
    public NetError error() {
        if (TextUtils.isEmpty(wxId))
            return NetError.noDataError();
        return null;
    }
}
