package com.jj.base.mvp;

import com.jj.base.net.NetError;

public interface IModel {
    NetError error();


    /**
     * presenter destory时默认调用
     */
//    void destory();
}
