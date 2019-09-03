package com.jj.comics.data.model;

import com.jj.base.mvp.IModel;
import com.jj.base.net.NetError;

/**
 * 网络请求返回的基础类
 */
public class ResponseModel implements IModel {

    //新版api返回值固定模型，就code和message两个字段
    protected int code;
    protected String message;

    @Override
    public NetError error() {
        if (code != 200){
            if (code == NetError.AuthError){
                return NetError.authError();
            }
            return new NetError(message, code);
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
