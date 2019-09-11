package com.jj.base.mvp;


import android.content.DialogInterface;

import com.jj.base.utils.toast.ToastUtil;

public interface IView {

    default void showProgress(){

    }

    default void showProgress(DialogInterface.OnDismissListener onDismissListener){

    }

    default void hideProgress(){

    }

    default void showToastShort(CharSequence msg){
        ToastUtil.showToastShort(msg);
    }

    default void showToastLong(CharSequence msg){
        ToastUtil.showToastLong(msg);
    }

}
